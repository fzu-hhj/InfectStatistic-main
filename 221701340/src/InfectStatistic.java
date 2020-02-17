package infectSta;

import java.io.*;

/**
 * InfectStatistic
 * TODO
 *
 * @author xxx
 * @version xxx
 * @since xxx
 */
class Date{
	private int year;
	private int month;
	private int day;
	Date(String dateStr){
		String date[] = dateStr.split("-");
		try {
			year = Integer.parseInt(date[0]);
			month = Integer.parseInt(date[1]);
			day = Integer.parseInt(date[2]);
		}catch(NumberFormatException e) {
			System.out.println(e);
		}
	}
	Date(){
		year = 2020;
		month = 12;
		day =31;
	}
	
	public int getYear() {
		return year;
	}
	public int getMonth(){
		return month;
	}
	public int getDay() {
		return day;
	}
	public boolean earlier(Date commandDate){
		if(year > commandDate.getYear()) {
			return false;
		}
		if(month > commandDate.getMonth()) {
			return false;
		}
		if(day > commandDate.getDay()) {
			return false;
		}
		return true;
	}
}
class Command {
	private Date date = new Date();
	private int  kinds = 4;//记录输出的人的种数
	private boolean ip = true;
	private boolean sp = true;
	private boolean cure = true;
	private boolean dead = true;
	private String[] province ;
	private String logFile;
	private String outFile;
	
	Command(String[] commandStr){
		
		if( ! (isCommand(commandStr) && parseCommand(commandStr))) {
			System.out.println("命令格式错误！");
		}	
	}
	
	public Date getDate() {
		return date;
	}
	public boolean getIp() {
		return ip;
	}
	public boolean getSp() {
		return sp;
	}
	public boolean getCure() {
		return cure;
	}
	public boolean getDead() {
		return dead;
	}
	public String[] getProvince() {
		return province;
	}
	public String getLogFile() {
		return logFile;
	}
	public String getOutFile() {
		return outFile;
	}
	public int getKinds() {
		return kinds;
	}
	
	/*初步判断命令的格式是否正确*/
	private boolean isCommand(String[] commandStr) {
		StringBuffer com = new StringBuffer();
		for(int i = 0; i < commandStr.length;i ++) {
			com.append(commandStr[i]+" ");
		}
		String regex = "list(\\s+-\\w+\\s+\\S+)*\\s+-log\\s+\\S+(\\s+-\\w+\\s+\\S+)*\\s+-out\\s+\\S+(\\s+-\\w+\\s+\\S+)*\\s*";
		if(!com.toString().matches(regex)) {
			System.out.println("01");//*******************
			return false;
		}
		return true;
	}
	/*解析并进一步检验格式*/
	private boolean parseCommand(String[] commandStr) {
		String regex = "-\\w+";
		String dateFormat = "\\d{4}-\\d{2}-\\d{2}";
		String provinceFormat = "([\u4e00-\u9fa5]+(\\S)?)+";
		String tempStr;
		for(int i = 0;i < commandStr.length;i ++) {
			tempStr = commandStr[i];
			if(tempStr.matches(regex) && i < commandStr.length-1) {
				if(tempStr.equals("-date") ) {
					//检验日期的格式是否合格
					if(!commandStr[i+1].matches(dateFormat)) {
						return false;
					}
					this.date = new Date(commandStr[i+1]);
				}
				if(tempStr.equals("-province")) {
					//检验省份的输入是否为中文
					if(!commandStr[i+1].matches(provinceFormat)) {
						return false;
					}
					this.province = changeInto(commandStr[i+1]);
				}
				if(tempStr.equals("-log")) {
					//检验输入文件名是否存在
					if(commandStr[i+1].matches(regex)) {
						return false;
					}
					this.logFile = commandStr[i+1];
				}
				if(tempStr.equals("-out")) {
					//检验输出文件名是否存在
					if(commandStr[i+1].matches(regex)) {
						return false;
					}
					this.outFile = commandStr[i+1];
				}
				if(tempStr.equals("-type")) {
					this.ip = false;
					this.sp = false;
					this.cure = false;
					this.dead = false;
					this.kinds = 0;
					for(int j = 0; j < 4;j ++) {
						if(commandStr[i+j].matches(regex)) {
							return false;
						}
						else if(commandStr[i+j].equals("ip")) {
							this.ip = true;
							this.kinds ++;
						}
						else if(commandStr[i+j].equals("sp")) {
							this.sp = true;
							this.kinds ++;
						}
						else if(commandStr[i+j].equals("cure")) {
							this.cure = true;
							this.kinds ++;
						}
						else if(commandStr[i+j].equals("dead")) {
							this.dead = true;
							this.kinds ++;
						}else {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	/*将省份字符串转化为字符串数组*/
	private String[] changeInto(String provinceStr) {
		String regex = "，";
		String[] province =provinceStr.split(regex);
		return province;
	}

}

class FileIO{
	public static final String[] PROVINCE = {"安徽","澳门","北京","重庆","福建","甘肃","广东","广西壮族"
			,"贵州","海南","河北","河南","黑龙江","湖北","湖南","江西","吉林","江苏","辽宁","内蒙古","宁夏回族","青海","山西","山东","陕西","上海","四川","天津","台湾","西藏"
			,"新疆","香港","云南","浙江"};
	private Command command;
	private int[][] num;//存储指定省的人数
	
	FileIO(Command command){
		this.command = command;
		/*int m = command.getProvince().length+1;
		if(command.getProvince().length == 0) {
			m = 35;//34个省和全国
		}*/
		int m = 35;//34个省和全国
		int n = 4;//4种人数
		num = new int[m][n] ;
		//初始化人数
		for(int i = 0;i < m ;i ++) {
			for(int j = 0;j < n;j ++) {
				num[i][j] = 0;
			}
		}
	}
	public void fileIn() {
		String encoding = "UTF-8";
		String dir = command.getLogFile();
		File[] files = new File(dir).listFiles();
		for(File file : files) {
			if(file.isFile() && file.exists()) {
				String fileDateStr = file.getName().substring(0, 10);
				System.out.println(fileDateStr);
				Date fileDate =new Date(fileDateStr);
				if(fileDate.earlier(command.getDate())) {
					//System.out.println("判断日期成功！");
					parseData(file , encoding);
				}
				//System.out.println("读入文件成功！");
			}
		}
		
	}

	//对每一个文件的数据进行读取
	private void parseData(File file , String encoding) {
		InputStreamReader read = null;
		try {
			read =new InputStreamReader(new FileInputStream(file), encoding);
			BufferedReader br = new BufferedReader(read);
			String text = null;
			//按行读取文件
			while((text = br.readLine()) != null) {
				if(text.indexOf("新增") != -1) {
					pp1(text);
				}
				if(text.indexOf("流入") !=-1){
					pp2(text);
				}
				if(text.indexOf("死亡") != -1) {
					pp3(text);
				}
				if(text.indexOf("治愈") != -1) {
					pp4(text);
				}
				if(text.indexOf("确认感染") != -1) {
					pp5(text);
				}
				if(text.indexOf("排除") != -1) {
					pp6(text);
				}
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
	}
	//处理新增感染患者和疑似患者
	private void pp1(String lineText) {
		String[] lineData = lineText.split("\\s+");
		int proNum = 0;
		//匹配省名
		for(int i = 0;i < 35;i ++) {
			if(PROVINCE[i].equals(lineData[0])) {
				proNum = i ;
				break;
			}		
		}
		if(lineData[2].equals("感染患者")) {
			int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("人") ));
			num[proNum][0] += icrement;
		}
		else if(lineData[2].equals("疑似患者")) {
			int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("人") ));
			num[proNum][1] += icrement;
		}
	}
	private void pp2(String lineText) {
		
	}
	private void pp3(String lineText) {
	
	}
	private void pp4(String lineText) {
	
	}
	private void pp5(String lineText) {
		
	}
	private void pp6(String lineText) {
		
	}
	private void pp7(String lineData) {
	
	}
	private void pp8(String lineData) {
		
	}
	public void fileOut() {
		
	}
}
public class InfectStatistic {

    public static void main(String[] args) {
    	Command command = new Command(args);
		FileIO fileIO = new FileIO(command);
		fileIO.fileIn();
		//fileIO.fileOut();
		System.out.println("程序结束！");
        System.out.println("helloworld");
    }
}
