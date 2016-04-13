package edu.cornell.mannlib.vitro.webapp.controller.jena;

public class APPartyRecord {

	private String personalTitle;
	private String givenName;
	private String surname;
	private String acadamicProfLink;
	private String position;
	private String divfac;
	private String school;
	private String discipline;
	private String email;
	private String phone;
	private String fax;
	private String membership;
	private String keywords;
	private String biography;
	private String ePrintsLink;
	private String awards;	// for now we don't get awards.
	private String userName;
	private String localKeyStr;
	
	
	public APPartyRecord(){
		personalTitle = "";
		givenName = "";
		surname = "";
		acadamicProfLink = "";
		position = "";
		divfac = "";
		school = "";
		discipline = "";
		email = "";
		phone = "";
		fax = "";
		membership = "";
		keywords = "";
		biography = "";
		ePrintsLink = "";
		awards = "";
		userName = "";
		localKeyStr = "";
	}
	
	public APPartyRecord( String personalTitle, String givenname, String surname, String position, String divfac, String school, String discipline, String email, String phone, String fax, String membership, String keywords, 
			String biography, String ePrintsLink, String userName, String localKeyStr){
		
		this.personalTitle = personalTitle;
		this.givenName = givenName;
		this.surname = surname;
		this.position = position;
		this.discipline = discipline;
		this.email = email;
		this.phone = phone;
		this.fax = fax;
		this.membership = membership;
		this.keywords = keywords;
		this.biography = biography;
		this.ePrintsLink = ePrintsLink;
		this.userName = userName;
		this.localKeyStr = localKeyStr ; //"10378.3/8085/1018." + localKey;
	}

	public String getPersonalTitle() {
		return personalTitle;
	}

	public void setPersonalTitle(String personalTitle) {
		this.personalTitle = personalTitle;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getAcadamicProfLink() {
		return acadamicProfLink;
	}

	public void setAcadamicProfLink(String acadamicProfLink) {
		this.acadamicProfLink = acadamicProfLink;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getDivfac() {
		return divfac;
	}

	public void setDivfac(String divfac) {
		this.divfac = divfac;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getDiscipline() {
		return discipline;
	}

	public void setDiscipline(String discipline) {
		this.discipline = discipline;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone1) {
		if (! phone1.equals("")){
			this.phone = "+61 7 " + phone1;
		}
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax1) {
		if (! fax1.equals("")){
			this.fax = "+61 7 " + fax1;
		}
	}

	public String getMembership() {
		return membership;
	}

	public void setMembership(String membership) {
		this.membership = membership;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public String getePrintsLink() {
		return ePrintsLink;
	}

	public void setePrintsLink(String ePrintsLink) {
		this.ePrintsLink = ePrintsLink;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLocalKeyStr() {
		return localKeyStr;
	}

	public void setLocalKeyStr(String localKeyStr) {
		this.localKeyStr = localKeyStr;
	}
}
