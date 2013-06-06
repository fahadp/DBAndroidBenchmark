package tools;

import java.text.SimpleDateFormat;

public class TransactionRow {

	//int transactionID, float price,String item,int buyer,int seller, String date
	
	public final int id;
	public final float price;
	public final String item;
	public final int buyer;
	public final int seller;
	public final String date;
	
	public PeopleRow buyer_object;
	public PeopleRow seller_object;
	
	public static int transactions = 0;

	
	public TransactionRow() {
		this(TransactionRow.transactions,FieldGenerator.randomPrice(),FieldGenerator.randomItem(),FieldGenerator.randomDate());
	}
	
	public TransactionRow(int transactionID) {
		this(transactionID,FieldGenerator.randomPrice(),FieldGenerator.randomItem(),FieldGenerator.randomDate());
	}
	
	public TransactionRow(float price,String item, String date) {
		this(TransactionRow.transactions,price,item,date);
	}
	
	public TransactionRow(int id, float price,String item, String date) {
		this.id = id ;
		this.price = price;
		this.item = item;
		this.buyer = PeopleRow.randomPerson();
		this.seller = PeopleRow.randomPerson();
		this.date = date;
		TransactionRow.transactions++;
	}
	/**
	 * insert into transactions (id,seller,buyer,price,item,date) values (?,?,?,?,?,?)
	 * @return
	 */
	public String[] bindArgs(){
		return new String[] {Integer.valueOf(this.id).toString(),Integer.valueOf(this.seller).toString(),Integer.valueOf(this.buyer).toString(),Float.valueOf(this.price).toString(),this.item,this.date};
	}
	
	public void setBuyerObject(PeopleRow buyer){
		this.buyer_object=buyer;
	}
	public void setSellerObject(PeopleRow seller){
		this.seller_object=seller;
	}
	
}
