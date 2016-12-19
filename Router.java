package FINAL;

import java.util.*;
import java.util.Map.Entry;


public class Router {
	String IPAddress;
	int    routerNumber;
	public boolean ON;
	Packet previousPacket;
	HashMap<Integer,Integer> connectingTable;
	HashMap<Integer,Information> routingTable;                      //Key is Destination RouterNumber, value is cost
	static int inf=Integer.MAX_VALUE;
	public static class Information{
		int nextRouter;
		int cost;
		public Information( int nextJump, int cost){

			this.nextRouter=nextJump;
			this.cost=cost;
		}

	}

	public Router() {
		
	}

/**********************************************************************************/
	//Constructor
	public Router(String IPAddress,int routerNumber)
	{
		this.IPAddress=IPAddress;
		this.routerNumber=routerNumber;
		this.ON=true;
		routingTable=new HashMap<Integer,Information>();
		connectingTable=Main.graph.findConnectingRouters(this.routerNumber);
	}
	
/*************************************************************************************************/
	//ReceivePacket Method
	private void ReceivePacket(Packet incommingPacket)
	{
		if(!this.ON)return;
		
		if(incommingPacket.TimeToLive==0)return;

		  this.previousPacket=incommingPacket;
		  /*if(incommingPacket.totalCost!=inf)*/{
			  if(routingTable.containsKey(incommingPacket.OriginatedID)){
				  Information oldInformation=routingTable.get(incommingPacket.OriginatedID);
				  if(oldInformation.cost>incommingPacket.totalCost){
					  //update routingTable
					  oldInformation.nextRouter=incommingPacket.FromID;
					  oldInformation.cost=incommingPacket.totalCost;
					  routingTable.put(incommingPacket.OriginatedID, oldInformation);
				  }
			  }else{
				  routingTable.put(incommingPacket.OriginatedID, new Information(incommingPacket.FromID,incommingPacket.totalCost));
			  }
		  }
		  Iterator<Entry<Integer, Integer>> iterator=connectingTable.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry entry = (Map.Entry) iterator.next();
				int val=(int)entry.getValue();
				
				Packet newpacket=new Packet(this.routerNumber,incommingPacket.SequenceNumber,incommingPacket.TimeToLive-1,incommingPacket.OriginatedID);
				newpacket.totalCost=incommingPacket.totalCost+val;
				Main.routerLinks.get((int)entry.getKey()).ReceivePacket(newpacket);
			}
	}
/*******************************************************************************************************/
	// 3 private methods here. They all serve the static method below, updateRouters()
	
	// originatedPacket() will send requests to all other routers to find connecting routers
	void originatedPacket(){
		for(int i=0;i<Main.routerLinks.size();i++){
			if( i==this.routerNumber) continue;
			int temp=Main.routerLinks.get(i).answer(this.routerNumber);
			if( temp != inf)
				connectingTable.put(i, temp);
			else
				connectingTable.remove(i);
		}
		routingTable.clear();
	}
	
	//answer() will examine the graph. If this router and parameter router are connected and this router is on. It will return the cost of between both routers
	private int answer(int routerNumber){
		if(this.ON && Main.graph.edges[this.routerNumber][routerNumber]>0)
			return Main.graph.edges[this.routerNumber][routerNumber];
		else 
			return inf;
	}
	
	private void sendPacket(){
		Main.routerLinks.get(this.routerNumber).ReceivePacket(new Packet(this.routerNumber,inf,10,this.routerNumber));
	}
	

/************************************************************************************************************/	
	 public static void updateRouters(){
		 for(Router r:Main.routerLinks){
			 r.originatedPacket();
		 }
		 for(Router r:Main.routerLinks){
			 r.sendPacket();
		 }
	 }
	 
/************************************************************************************************************/	
	 public void displayRoutingTable(){
			System.out.println("***************************************");
			System.out.println("Router Table "+this.routerNumber+": ");
			Iterator<Entry<Integer, Information>> iterator=routingTable.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry entry = (Map.Entry) iterator.next();
				Information temp=(Information)entry.getValue();
				if(temp.nextRouter==this.routerNumber){
					continue;
				}
				
				System.out.println("network "+entry.getKey()+", "+"cost "+temp.cost +", "+ "outgoing link "+temp.nextRouter);
			}
	}
	 
}

