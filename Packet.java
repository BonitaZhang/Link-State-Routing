package FINAL;

public class Packet {
	int FromID;
	int OriginatedID;
	int SequenceNumber=0;     //Indicates how many packets have been sent from the originated router;
	int TimeToLive=0;
	int totalCost=0;
	static int initTimeToLive=10;
	
	
	public Packet(int id,int sequenceNumber,int timeToLive,int OriginateID)
	{
		this.FromID=id;
		this.OriginatedID=OriginateID;
		this.SequenceNumber=sequenceNumber;
		this.TimeToLive=timeToLive;
		
	}
	

}
