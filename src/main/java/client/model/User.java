package client.model;

/**
 *
 * @author user
 */
public class User {
	private String id;
	private String name;
	private String password;
	private String role;
	private String phone;

	public User(String id, String role, String phone){
		this(id, "", "", role, phone);
	}

	public User(String id, String name, String password, String role, String phone){
		this.id = id;
		this.name = name == null ? "" : name.trim();
		this.password = password == null ? "" : password;
		this.role = role;
		this.phone = phone == null ? "" : phone.trim();
	}

	public String getId(){ return id; }
	public String getName(){ return name; }
	public String getPassword(){ return password; }
	public String getRole(){ return role; }
	public String getPhone(){ return phone; }
}
