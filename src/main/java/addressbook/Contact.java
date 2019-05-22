package addressbook;

import org.json.JSONObject;

public class Contact {
	private ContactField[] fields;
	private String errorMsg;
	
	public Contact(String inputJson) {
		fields = new ContactField[7];
		fields[0] = new ContactField("name", 100);			// All possible fields and their max lengths
		fields[1] = new ContactField("address", 200);
		fields[2] = new ContactField("city", 100);
		fields[3] = new ContactField("state", 100);
		fields[4] = new ContactField("postal_code", 9);
		fields[5] = new ContactField("phone", 20);
		fields[6] = new ContactField("email", 318);
		errorMsg = "";
		
		readJson(inputJson);
	}
	
	private void readJson(String inputJson) {
		JSONObject contactJson = new JSONObject(inputJson);
		
		for(int i = 0; i < fields.length; i++) {
			if (contactJson.has(fields[i].fieldType)) {
				if (fields[i].fieldType.equals("postal_code")) {
					fields[i].setField(stripPostalCode(contactJson.getString(fields[i].fieldType)));	// Strip invalid characters from postal code
				}
				else if (fields[i].fieldType.equals("phone")) {
					fields[i].setField(stripPhoneNumber(contactJson.getString(fields[i].fieldType)));	// and phone number
				}
				else {
					fields[i].setField(contactJson.getString(fields[i].fieldType));
				}
				
				errorMsg += fields[i].getErrorMsg();
			}
		}
	}
	
	public String getJson() {
		JSONObject outJson = new JSONObject();
		for (int i = 0; i < fields.length; i++) {
			outJson.put(fields[i].fieldType, fields[i].getField());
		}
		
		return outJson.toString();
	}
	
	public String getPartialJson() {
		JSONObject outJson = new JSONObject();
		for (int i = 0; i < fields.length; i++) {
			if (!fields[i].equals("")) {
				outJson.put(fields[i].fieldType, fields[i].getField());
			}
		}
		
		return outJson.toString();
	}
	
	public String getName() {
		return fields[0].getField();
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	private String stripPostalCode(String input) {
		return input.replaceAll("[^a-zA-Z0-9]", "");
	}
	
	private String stripPhoneNumber(String input) {
		return input.replaceAll("[^a-dA-D0-9*#]", "");
	}
}
