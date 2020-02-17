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
	private int  kinds = 4;//��¼������˵�����
	private boolean ip = true;
	private boolean sp = true;
	private boolean cure = true;
	private boolean dead = true;
	private String[] province ;
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
	public static final String[] PROVINCE = {"ȫ��","����","����","����","����","����","����","�㶫","����׳��"
			,"����","����","�ӱ�","����","������","����","����","����","����","����","����","���ɹ�","���Ļ���","�ຣ","ɽ��","ɽ��","����","�Ϻ�","�Ĵ�","���","̨��","����"
			,"�½�","���","����","�㽭"};
	private Command command;
	private int[][] num;//�洢ָ��ʡ������
	
	FileIO(Command command){
		this.command = command;
		/*int m = command.getProvince().length+1;
		if(command.getProvince().length == 0) {
			m = 35;//34��ʡ��ȫ��
		}*/
		int m = 35;//34��ʡ��ȫ��
		int n = 4;//4������
		num = new int[m][n] ;
		//��ʼ������
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
					//System.out.println("�ж����ڳɹ���");
					parseData(file , encoding);
				}
				//System.out.println("�����ļ��ɹ���");
			}
		}
		
	}

	//��ÿһ���ļ������ݽ��ж�ȡ
	private void parseData(File file , String encoding) {
		InputStreamReader read = null;
		try {
			read =new InputStreamReader(new FileInputStream(file), encoding);
			BufferedReader br = new BufferedReader(read);
			String text = null;
			//���ж�ȡ�ļ�
			while((text = br.readLine()) != null ) {
				String[] lineData = text.split("\\s+");
				if(lineData[0].equals("//")) {
					break;
				}
				int proNum = 0;
				//ƥ��ʡ��
				for(int i = 0;i < 35;i ++) {
					if(PROVINCE[i].equals(lineData[0])) {
						proNum = i ;
						break;
					}		
				}
				if(text.indexOf("����") != -1) {
					newPatient(lineData , proNum);
				}
				if(text.indexOf("����") !=-1){
					patientFlow(lineData , proNum);
				}
				if(text.indexOf("����") != -1) {
					patientDie(lineData , proNum);
				}
				if(text.indexOf("����") != -1) {
					patientCure(lineData , proNum);
				}
				if(text.indexOf("ȷ���Ⱦ") != -1) {
					confirmIfection(lineData , proNum);
				}
				if(text.indexOf("�ų�") != -1) {
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
	//����������Ⱦ���ߺ����ƻ���
	private void newPatient(String[] lineData ,int proNum ) {
		if(lineData[2].equals("��Ⱦ����")) {
			int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("��") ));
			num[proNum][0] += icrement;
		}
		else if(lineData[2].equals("���ƻ���")) {
			int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("��") ));
			num[proNum][1] += icrement;
		}
	}
	//��������
	private void patientFlow(String[] lineData ,int proNumOut ) {
		int proNumIn = 0;
		for(int i = 0;i < 35;i ++) {
			if(PROVINCE[i].equals(lineData[3])) {
				proNumIn = i ;
				break;
			}		
		}
		if(lineData[1].equals("��Ⱦ����")) {
			int icrement = Integer.parseInt(lineData[4].substring(0,lineData[4].indexOf("��") ));
			num[proNumOut][0] -= icrement;
			num[proNumIn][0] += icrement;
		}
		if(lineData[1].equals("���ƻ���")) {
			int icrement = Integer.parseInt(lineData[4].substring(0,lineData[4].indexOf("��") ));
			num[proNumOut][1] -= icrement;
			num[proNumIn][1] += icrement;
		}
	}
	//������������
		private void patientCure(String[] lineData ,int proNum ) {
			int icrement = Integer.parseInt(lineData[2].substring(0,lineData[2].indexOf("��") ));
			num[proNum][0] -= icrement;
			num[proNum][2] += icrement;
		}
	//������������
	private void patientDie(String[] lineData ,int proNum ) {
		int icrement = Integer.parseInt(lineData[2].substring(0,lineData[2].indexOf("��") ));
		num[proNum][0] -= icrement;
		num[proNum][3] += icrement;
	}
	//����ȷ�ϸ�Ⱦ������
	private void confirmIfection(String[] lineData ,int proNum ) {
		int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("��") ));
		num[proNum][0] += icrement;
		num[proNum][1] -= icrement;
	}
	//�����ų���Ⱦ������
	private void excludeIfection(String[] lineData ,int proNum ) {
		int icrement = Integer.parseInt(lineData[3].substring(0,lineData[3].indexOf("��") ));
		num[proNum][1] -= icrement;
	}
	//����ȫ��������
	private void nationCount() {
		//���ȫ��������
		for(int i = 0;i < 4;i ++) {
			num[0][i] = 0;
		}
		for(int i = 1;i < 35 ; i ++ ) {
			for(int j = 0;j< 4;j ++) {
				num[0][j] += num[i][j];
			}
		}
	}
	//����ļ�
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
							bufferWriter.write(PROVINCE[i] + " " +"��Ⱦ����" + num[i][0] + "��"
									+ " " + "���ƻ���" + num[i][1] + "��" + " " + "����" + num[i][2] + "��"
									+ " " + "����" + num[i][3] + "��" + "\n");
							System.out.println(PROVINCE[i] + " " +"��Ⱦ����" + num[i][0] + "��"
									+ " " + "���ƻ���" + num[i][1] + "��" + " " + "����" + num[i][2] + "��"
									+ " " + "����" + num[i][3] + "��" + "\n");
						}
					}
				}else {
					bufferWriter.write(PROVINCE[i] + " " +"��Ⱦ����" + num[i][0] + "��"
							+ " " + "���ƻ���" + num[i][1] + "��" + " " + "����" + num[i][2] + "��"
							+ " " + "����" + num[i][3] + "��");
				}
			}
			bufferWriter.write("// ���ĵ�������ʵ���ݣ���������ʹ��");
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
		System.out.println("���������");
    }
}
