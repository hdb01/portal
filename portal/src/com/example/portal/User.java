package com.example.portal;

public class User {

	private String nom;
	private String phone;
	private String image;
	private String token;
	private String uidCompany;
	private String nameCompany;
	public String getUidCompany() {
		return uidCompany;
	}
	public void setUidCompany(String uidCompany) {
		this.uidCompany = uidCompany;
	}
	public String getNameCompany() {
		return nameCompany;
	}
	public void setNameCompany(String nameCompany) {
		this.nameCompany = nameCompany;
	}
	private String[] partners;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	public User(String _nom, String _telephone , String _image, String _token, String _uidcomp, String _namecomp) {
		nom=_nom;
		phone=_telephone;
		image=_image;
		token=_token;
		uidCompany = _uidcomp;
		nameCompany =_namecomp;
		
	}
	public String[] getPartners() {
		return partners;
	}
	public void setPartners(String[] partners) {
		this.partners = partners;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
