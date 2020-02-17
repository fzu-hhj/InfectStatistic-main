package infectSta;

import java.io.File;

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
			System.out.println("02");///////////////////////*
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
	private int  kinds = 4;//��¼������˵�����
	private boolean ip = true;
	private boolean sp = true;
	private boolean cure = true;
	private boolean dead = true;
	private String[] province = {"ȫ��","����","����"};
	private String logFile;
	private String outFile;
	
	Command(String[] commandStr){
		
		if( ! (isCommand(commandStr) && parseCommand(commandStr))) {
			System.out.println("�����ʽ����");
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
	
	/*�����ж�����ĸ�ʽ�Ƿ���ȷ*/
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
	/*��������һ�������ʽ*/
	private boolean parseCommand(String[] commandStr) {
		String regex = "-\\w+";
		String dateFormat = "\\d{4}-\\d{2}-\\d{2}";
		String provinceFormat = "([\u4e00-\u9fa5]+(\\S)?)+";
		String tempStr;
		for(int i = 0;i < commandStr.length;i ++) {
			tempStr = commandStr[i];
			if(tempStr.matches(regex) && i < commandStr.length-1) {
				if(tempStr.equals("-date") ) {
					//�������ڵĸ�ʽ�Ƿ�ϸ�
					if(!commandStr[i+1].matches(dateFormat)) {
						return false;
					}
					this.date = new Date(commandStr[i+1]);
				}
				if(tempStr.equals("-province")) {
					//����ʡ�ݵ������Ƿ�Ϊ����
					if(!commandStr[i+1].matches(provinceFormat)) {
						return false;
					}
					this.province = changeInto(commandStr[i+1]);
				}
				if(tempStr.equals("-log")) {
					//���������ļ����Ƿ����
					if(commandStr[i+1].matches(regex)) {
						return false;
					}
					this.logFile = commandStr[i+1];
				}
				if(tempStr.equals("-out")) {
					//��������ļ����Ƿ����
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
	/*��ʡ���ַ���ת��Ϊ�ַ�������*/
	private String[] changeInto(String provinceStr) {
		String regex = "��";
		String[] province =provinceStr.split(regex);
		return province;
	}

}

class FileIO{
	private Command command;
	private int[][] num;//�洢ָ��ʡ������
	FileIO(Command command){
		this.command = command;
		int m = command.getProvince().length;
		int n = command.getKinds();
		num = new int[m][n] ;
	}
	public void fileIn() {
		System.out.println("01");
		String encoding = "UTF-8";
		String dir = command.getLogFile();
		File[] files = new File(dir).listFiles();
		for(File file : files) {
			if(file.isFile() && file.exists()) {
				String fileDateStr = file.getName().substring(0, 10);
				System.out.println(fileDateStr);
				Date fileDate =new Date(fileDateStr);
				if(fileDate.earlier(command.getDate())) {
					//System.out.println("�ж����ڳɹ���");
					
				}
				//System.out.println("�����ļ��ɹ���");
			}
		}
		
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
		System.out.println("���������");
        System.out.println("helloworld");
    }
}
