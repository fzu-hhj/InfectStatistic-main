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
			e.printStackTrace();
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
	public static final String[] PROVINCE = {"全国","安徽","澳门","北京","重庆","福建","甘肃","广东","广西壮族"
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
			while((text = br.readLine()) != null ) {
				String[] lineData = text.split("\\s+");
				if(lineData[0].equals("//")) {
					break;
				}
				int proNum = 0;
				//匹配省名
				for(int i = 0;i < 35;i ++) {
					if(PROVINCE[i].equals(lineData[0])) {
						proNum = i ;
						break;
					}		
				}
				if(text.indexOf("新增") != -1) {
					newPatient(lineData , proNum);
				}
				if(text.indexOf("流入") !=-1){
					patientFlow(lineData , proNum);
				}
				if(text.indexOf("死亡") != -1) {
					patientDie(lineData , proNum);
				}
				if(text.indexOf("治愈") != -1) {
					patientCure(lineData , proNum);
				}
				if(text.indexOf("确诊感染") != -1) {
					confirmIfection(lineData , proNum);
				}
				if(text.indexOf("排除") != -1) {
					excludeIfection(lineData , proNum);
				}
			}
			nationCount();
			read.close();
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
	}
	//处理新增感染患者和疑似患者
	private void newPatient(String[] lineData ,int proNum ) {
		if(lineData[2].equals("感染患者")) {
			int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("人") ));
			num[proNum][0] += icrement;
		}
		else if(lineData[2].equals("疑似患者")) {
			int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("人") ));
			num[proNum][1] += icrement;
		}
	}
	//处理流入
	private void patientFlow(String[] lineData ,int proNumOut ) {
		int proNumIn = 0;
		for(int i = 0;i < 35;i ++) {
			if(PROVINCE[i].equals(lineData[3])) {
				proNumIn = i ;
				break;
			}		
		}
		if(lineData[1].equals("感染患者")) {
			int icrement = Integer.parseInt(lineData[4].substring(0,lineData[4].indexOf("人") ));
			num[proNumOut][0] -= icrement;
			num[proNumIn][0] += icrement;
		}
		if(lineData[1].equals("疑似患者")) {
			int icrement = Integer.parseInt(lineData[4].substring(0,lineData[4].indexOf("人") ));
			num[proNumOut][1] -= icrement;
			num[proNumIn][1] += icrement;
		}
	}
	//处理治愈的人
		private void patientCure(String[] lineData ,int proNum ) {
			int icrement = Integer.parseInt(lineData[2].substring(0,lineData[2].indexOf("人") ));
			num[proNum][0] -= icrement;
			num[proNum][2] += icrement;
		}
	//处理死亡的人
	private void patientDie(String[] lineData ,int proNum ) {
		int icrement = Integer.parseInt(lineData[2].substring(0,lineData[2].indexOf("人") ));
		num[proNum][0] -= icrement;
		num[proNum][3] += icrement;
	}
	//处理确认感染的数据
	private void confirmIfection(String[] lineData ,int proNum ) {
		int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("人") ));
		num[proNum][0] += icrement;
		num[proNum][1] -= icrement;
	}
	//处理排除感染的数据
	private void excludeIfection(String[] lineData ,int proNum ) {
		int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("人") ));
		num[proNum][1] -= icrement;
	}
	//计算全国的数据
	private void nationCount() {
		//清空全国的数据
		for(int i = 0;i < 4;i ++) {
			num[0][i] = 0;
		}
		for(int i = 1;i < 35 ; i ++ ) {
			for(int j = 0;j< 4;j ++) {
				num[0][j] += num[i][j];
			}
		}
	}
	//输出文件
	public void fileOut() {
		try {
			File file = new File(command.getOutFile());
			if(!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			for(int i = 0;i < 35;i ++) {
				if(num[i][0] == 0 && num[i][1] == 0) {
					continue;
				}
				if(command.getProvince().length != 0) {
					for(int k = 0;k < command.getProvince().length;k ++) {
						if(PROVINCE[i].equals(command.getProvince()[k])) {
							bufferWriter.write(PROVINCE[i] + " " +"感染患者" + num[i][0] + "人"
									+ " " + "疑似患者" + num[i][1] + "人" + " " + "治愈" + num[i][2] + "人"
									+ " " + "死亡" + num[i][3] + "人" + "\n");
							System.out.println(PROVINCE[i] + " " +"感染患者" + num[i][0] + "人"
									+ " " + "疑似患者" + num[i][1] + "人" + " " + "治愈" + num[i][2] + "人"
									+ " " + "死亡" + num[i][3] + "人" + "\n");
						}
					}
				}else {
					bufferWriter.write(PROVINCE[i] + " " +"感染患者" + num[i][0] + "人"
							+ " " + "疑似患者" + num[i][1] + "人" + " " + "治愈" + num[i][2] + "人"
							+ " " + "死亡" + num[i][3] + "人");
				}
			}
			bufferWriter.write("// 该文档并非真实数据，仅供测试使用");
			bufferWriter.close();
		}catch(IOException e){
		      e.printStackTrace();
	     }
		
	}
}
public class InfectStatistic {

    public static void main(String[] args) {
    	Command command = new Command(args);
		FileIO fileIO = new FileIO(command);
		fileIO.fileIn();
		fileIO.fileOut();
		System.out.println("程序结束！");
    }
}
