package addressbook;

import static spark.Spark.*;

public class Server {
	
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_PAGE_NUMBER = 1;
	public static final String INDEX_NAME = "contacts";
	
	Database database;
	Controller controller;
	
	public Server() {
		
		database = new Database();
		controller = new Controller(database, INDEX_NAME);

		get("/contact", (req, res) -> listAllContacts(req.queryParams("pageSize"), req.queryParams("page"), req.queryParams("query")));
	   
	    post("/contact", (req, res) -> createContact(req.body()));
	    
	    get("/contact/*", (req, res) -> getContact(req.splat()[0]));
	    
	    put("/contact/*", (req, res) -> updateContact(req.splat()[0], req.body()));
	    
	    delete("/contact/*", (req, res) -> deleteContact(req.splat()[0]));
	}
	
	private String listAllContacts(String pageSizeStr, String pageNumberStr, String query) {
		int pageSize = DEFAULT_PAGE_SIZE;
		try {													// Restrict page size to a sensible number
			pageSize = Integer.parseInt(pageSizeStr);
			if (pageSize < 1)
				pageSize = DEFAULT_PAGE_SIZE;
		} catch (NumberFormatException e) {}
		
		int pageNumber = DEFAULT_PAGE_NUMBER;
		try {													// Same for page number
			pageNumber = Integer.parseInt(pageNumberStr);
			if (pageNumber < 1)
				pageNumber = DEFAULT_PAGE_NUMBER;
		} catch (NumberFormatException e) {}
		
		if (query == null)
			query = "*";
		return controller.listAll(pageSize, pageNumber, query);
	}
	
	private String createContact(String body) {
		Contact newContact = new Contact(body);
		if (newContact.getErrorMsg().equals("")) {
			if(!controller.doesNameExist(newContact.getName().toLowerCase())) {
				controller.addEntry(newContact.getJson(), newContact.getName());
				return newContact.getName() + " added.";
			}
			else
				return "Name " + newContact.getName() + " already exists.";
		}
		else
			return newContact.getErrorMsg();
	}
	
	private String getContact(String inputContact) {
		return controller.getName(inputContact);
	}
	
	private String updateContact(String inputContact, String body) {
		Contact updatedContact = new Contact(body);
		if (updatedContact.getErrorMsg().equals("")) {
			if(controller.doesNameExist(inputContact)) {
				controller.updateEntry(body, inputContact);
				return "";
			}
			else
				return "Name " + inputContact + " does not exist.";
		}
		else
			return updatedContact.getErrorMsg();
	}
	
	private String deleteContact(String inputContact) {
		if (controller.doesNameExist(inputContact)) {
			controller.deleteEntry(inputContact);
		}
		else
			return "Name " + inputContact + " does not exist.";
		return "";
	}
}
