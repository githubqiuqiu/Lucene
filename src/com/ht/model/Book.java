package com.ht.model;

import java.math.BigDecimal;

/**
* @author Qiu
* @time  2018年2月28日 下午2:49:27
* 
*/

public class Book {
	private Integer id;
	private String bname;
	private BigDecimal price;
	private String pic;
	private String description;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBname() {
		return bname;
	}
	public void setBname(String bname) {
		this.bname = bname;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "Book [id=" + id + ", bname=" + bname + ", price=" + price + ", pic=" + pic + ", description="
				+ description + "]";
	}
	public Book(Integer id, String bname, BigDecimal price, String pic, String description) {
		super();
		this.id = id;
		this.bname = bname;
		this.price = price;
		this.pic = pic;
		this.description = description;
	}
	public Book() {
		super();
	}
	
	
}
