package pl.pisz.airlog.giepp.android;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;

import pl.pisz.airlog.giepp.data.ArchivedStock;
import pl.pisz.airlog.giepp.data.CurrentStock;
import pl.pisz.airlog.giepp.data.LocalStorage;
import pl.pisz.airlog.giepp.data.PlayerStock;
import pl.pisz.airlog.giepp.data.gpw.GPWDataParser;
import pl.pisz.airlog.giepp.data.gpw.GPWDataSource;
import pl.pisz.airlog.giepp.game.ActionException;
import pl.pisz.airlog.giepp.game.Game;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GiePPSingleton{
	
	private static GiePPSingleton instance = null;
	
	public static GiePPSingleton getInstance() {
		if (GiePPSingleton.instance == null) GiePPSingleton.instance = new GiePPSingleton();
		return GiePPSingleton.instance;
	}
	
	private String name;
	private Game game;
	private AllRecordsAdapter adapter1;
	private ObservedAdapter adapter2;
	private AccountAdapter adapter3;
	private Activity act;
	private boolean refreshing;
	private MyAccountFragment fragment1;
	
	private GiePPSingleton() {
		try{
			File file1 = File.createTempFile("file1", "xml");
			File file2 = File.createTempFile("file2", "xml");
			File file3 = File.createTempFile("file3", "xml");
			File file4 = File.createTempFile("stats", "xml");
			game = new Game(new GPWDataSource(),new GPWDataParser(),LocalStorage.newInstance(file1, file2, file3, file4));
			this.refreshing = false;
		}catch(IOException e){
			Log.e("giepp","Nie mozna stworzyc plikow");
			System.exit(1);
		}
	}
	public void refresh(){
		if (refreshing) {
				return;
		}
		refreshing = true;
		(new Thread() {
			
			@Override
			public void run(){
				try{
					Log.i("giepp","Zaczynam sciagac");
					game.refreshData();
					Set<String> keys = game.getArchived().keySet();
					for(String k: keys){
						ArrayList<ArchivedStock> l = game.getArchived().get(k);
						for(int i = 0; i<l.size(); i++)
							Log.i("giepp",l.get(i).getName()+": "+l.get(i).getMaxPrice());
					}
					Log.i("giepp","Dane sciagniete");
										
					act.runOnUiThread(new Runnable(){
						public void run(){
							if(adapter1 != null){		
								adapter1.zmiana(game.getCurrent());
								Log.i("giepp","lista1 updatowana");
							}
							else{
								Log.i("giepp","adapter1 null");
							}
							if(adapter2 != null){
								adapter2.zmiana(game.getCurrent(),game.getObserved());
								Log.i("giepp","lista2 updatowana");
							}
							else{
								Log.i("giepp","adapter2 null");
							}
							if(adapter3 != null){		
								adapter3.zmiana(game.getOwned());
								Log.i("giepp","lista3 updatowana");
							}
							else{
								Log.i("giepp","adapter3 null");
							}
						}
					});
				}catch(Exception e){
					Log.i("giepp","Blad"+e);
				}
				refreshing = false;
			}
		}).start();
	}
	
	public void buy(String companyName, int amount){
		try{
			game.buy(companyName,amount);
		}catch( Exception e){
			Log.i("giepp","Blad1: " + e);
		}
		try {
			act.runOnUiThread(new Runnable(){
				public void run(){
					if (fragment1 != null) {
						fragment1.zmiana();
						Log.i("giepp","moje konto updatowane");
					}
					if (adapter3 != null) {		
						adapter3.zmiana(game.getOwned());
						Log.i("giepp","lista3 updatowana");
					}
				}
			});
		}catch(Exception e){
			Log.i("giepp","Blad2: " + e);
		}
	}
	public void sell(String companyName, int amount){
		try{
			game.sell(companyName,amount);
			act.runOnUiThread(new Runnable(){
				public void run(){
					if (fragment1 != null) {
						fragment1.zmiana();
						Log.i("giepp","moje konto updatowane");
					}
					if(adapter3 != null){		
						adapter3.zmiana(game.getOwned());
						Log.i("giepp","lista3 updatowana");
					}
				}
			});
		}
		catch(Exception e){
			Log.i("giepp","nie udalo sie: " + e);
		}
	}
	
	public ArrayList<CurrentStock> getCurrent(){
		return game.getCurrent();
	}

	public long getMoney() {
		return game.getMoney();
	}
	public long getMoneyInStock() {
		return game.getMoneyInStock();
	}
	public ArrayList<PlayerStock> getOwned(){
		return game.getOwned();
	}
	public void addToObserved(String name){
		game.addToObserved(name);
		act.runOnUiThread(new Runnable(){
			public void run(){
				if(adapter2 != null){		
					adapter2.zmiana(game.getCurrent(),game.getObserved());
					Log.i("giepp","lista2 updatowana");
				}
			}
		});
	}
	public void removeFromObserved(String name) {
		game.removeFromObserved(name);
		act.runOnUiThread(new Runnable(){
			public void run(){
				if(adapter2 != null){		
					adapter2.zmiana(game.getCurrent(),game.getObserved());
					Log.i("giepp","lista2 updatowana");
				}
			}
		});
	}
	public ArrayList<String> getObserved(){
		return game.getObserved();
	}
	public void setFragment1(MyAccountFragment fragment1){
		this.fragment1 = fragment1;
	}
	
	public void setAdapter1(AllRecordsAdapter adapter1){
		this.adapter1 = adapter1;
	}
	public void setAdapter2(ObservedAdapter adapter2){
		this.adapter2 = adapter2;
	}
	public void setAdapter3(AccountAdapter adapter3){
		this.adapter3 = adapter3;
	}
	public void setActivity(Activity act){
		this.act = act;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
}