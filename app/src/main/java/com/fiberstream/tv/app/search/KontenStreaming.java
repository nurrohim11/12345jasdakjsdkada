package com.fiberstream.tv.app.search;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KontenStreaming implements Serializable {

	@SerializedName("id")
	private String id;

	@SerializedName("id_kategori")
	private String idKategori;

	@SerializedName("nama")
	private String nama;

	@SerializedName("link")
	private String link;

	@SerializedName("icon")
	private String icon;

	@SerializedName("package")
	private String jsonMemberPackage;

	@SerializedName("url_playstore")
	private String urlPlaystore;

	@SerializedName("url_web")
	private String urlWeb;

	@SerializedName("flag")
	private String flag;

	public KontenStreaming(String id, String idKategori, String nama, String link, String  icon, String jsonMemberPackage, String urlPlaystore, String urlWeb, String flag){
		this.id =id;
		this.idKategori = idKategori;
		this.nama= nama;
		this.link = link;
		this.icon = icon;
		this.jsonMemberPackage = jsonMemberPackage;
		this.urlPlaystore = urlPlaystore;
		this.urlWeb = urlWeb;
		this.flag = flag;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setIdKategori(String idKategori){
		this.idKategori = idKategori;
	}

	public String getIdKategori(){
		return idKategori;
	}

	public void setNama(String nama){
		this.nama = nama;
	}

	public String getNama(){
		return nama;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setIcon(String icon){
		this.icon = icon;
	}

	public String getIcon(){
		return icon;
	}

	public void setJsonMemberPackage(String jsonMemberPackage){
		this.jsonMemberPackage = jsonMemberPackage;
	}

	public String getJsonMemberPackage(){
		return jsonMemberPackage;
	}

	public void setUrlPlaystore(String urlPlaystore){
		this.urlPlaystore = urlPlaystore;
	}

	public String getUrlPlaystore(){
		return urlPlaystore;
	}

	public void setUrlWeb(String urlWeb){
		this.urlWeb = urlWeb;
	}

	public String getUrlWeb(){
		return urlWeb;
	}

	public void setFlag(String flag){
		this.flag = flag;
	}

	public String getFlag(){
		return flag;
	}

	@Override
 	public String toString(){
		return 
			"ResponseKontenStreaming{" + 
			"id = '" + id + '\'' + 
			",id_kategori = '" + idKategori + '\'' + 
			",nama = '" + nama + '\'' + 
			",link = '" + link + '\'' + 
			",icon = '" + icon + '\'' + 
			",package = '" + jsonMemberPackage + '\'' + 
			",url_playstore = '" + urlPlaystore + '\'' + 
			",url_web = '" + urlWeb + '\'' + 
			",flag = '" + flag + '\'' + 
			"}";
		}
}