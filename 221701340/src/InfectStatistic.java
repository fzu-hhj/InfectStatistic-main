package infectSta;
/**
 * InfectStatistic
 * TODO
 *
 * @author xxx
 * @version xxx
 * @since xxx
 */
class Command {
	private String date = "latest";
	private boolean ip = true;
	private boolean sp = true;
	private boolean cure = true;
	private boolean dead = true;
	private String[] province;
	private String logFile;
	private String outFile;
	Command(String[] commandStr){
		
		if( ! (isCommand(commandStr) && parseCommand(commandStr))) {
			System.out.println("命令格式错误！");
		}	
	}
	public String getDate() {
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
					this.date = commandStr[i+1];
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
					for(int j = 0; j < 4;j ++) {
						if(commandStr[i+j].matches(regex)) {
							return false;
						}
						else if(commandStr[i+j].equals("ip")) {
							this.ip = true;
						}
						else if(commandStr[i+j].equals("sp")) {
							this.sp = true;
						}
						else if(commandStr[i+j].equals("cure")) {
							this.cure = true;
						}
						else if(commandStr[i+j].equals("dead")) {
							this.dead = true;
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
public class InfectStatistic {
	

    public static void main(String[] args) {
    	Command command = new Command(args);
		System.out.println(command.getDate());
		System.out.println("程序结束！");
        System.out.println("helloworld");
    }
}
