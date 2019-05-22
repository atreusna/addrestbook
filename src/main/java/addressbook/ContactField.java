package addressbook;

public class ContactField {
	public final String fieldType;
	public final int maxLength;
	private String fieldText;
	private String errorMsg;
	
	public ContactField(String fieldType, int maxLength) {
		this.fieldType = fieldType;
		this.maxLength = maxLength;
		this.fieldText = "";
		this.errorMsg = "";
	}
	
	public boolean setField(String input) {
		fieldText = input;
		return verifyField();
	}
	
	public String getField() {
		return fieldText;
	}
	
	private boolean verifyField() {
		if (fieldText.length() > maxLength)
			errorMsg += "Field " + fieldType + " is too long.\n";
		return (errorMsg.length() > 0) ? false : true;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
}
