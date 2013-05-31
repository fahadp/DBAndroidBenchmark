package tools;

public class TransactionRow {

	//int transactionID, float price,String item,int buyer,int seller, String date
	
	public final int transactionID;
	public final float price;
	public final String item;
	public final int buyer;
	public final int seller;
	public final String date;
	
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
	
	public TransactionRow(int transactionID, float price,String item, String date) {
		this.transactionID = transactionID ;
		this.price = price;
		this.item = item;
		this.buyer = PeopleRow.randomPerson();
		this.seller = PeopleRow.randomPerson();
		this.date = date;
		TransactionRow.transactions++;
	}
	
}
