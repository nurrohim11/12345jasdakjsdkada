package com.fiberstream.tv.app.main.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class DataRowModel implements Serializable {

	@SerializedName("id")
	private int id;

	@SerializedName("kategori")
	private String kategori;

	@SerializedName("data")
	private List<DataModel> data;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setKategori(String kategori){
		this.kategori = kategori;
	}

	public String getKategori(){
		return kategori;
	}

	public void setData(List<DataModel> data){
		this.data = data;
	}

	public List<DataModel> getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"DataRowModel{" +
			"id = '" + id + '\'' + 
			",kategori = '" + kategori + '\'' + 
			",data = '" + data + '\'' + 
			"}";
		}
}