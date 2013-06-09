package com.example.rfid;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Profile {
	
	private int id;
	private String name;
	private List<Property> properties;
	private List<String> valeurs;
	
	// Constructeur par défaut
	public Profile()
	{
		this.id=0;
		this.name = "";
		this.properties = null;
	}
	
	// Initialisation de la liste de propriétés
	public void initProperties()
	{
		this.properties = new ArrayList<Property>();
	}
	
	// Assesseur sur les propriétes
	public List<Property> getProperties() {
		return this.properties;
	}
	
	// Ajouter une propriété
	public void addProperty(String name, String type, String required)
	{
		Property.Type ptype = Property.Type.String;
		boolean req = false;
		
		if (type.equals("string"))
		{
			ptype = Property.Type.String;
		}
		else if (type.equals("number"))
		{
			ptype = Property.Type.Number;
		}
		else if (type.equals("boolean"))
		{
			ptype = Property.Type.Boolean;
		}
		else if (type.equals("boolean"))
		{
			ptype = Property.Type.Date;
		}
		
		if(required.equals("true"))
		{
			req=true;
		}
		
		properties.add(new Property(name, ptype,req));
	}
	
	// Modificateur sur l'attribut id
	public void setId(String id)
	{
		this.id = Integer.parseInt(id);
	}
	
	// Modificateur sur l'attribut name
	public void setName(String name)
	{
		this.name = name;
	}

	// Assesseur sur l'attribut id
	public int getId() {
		return this.id;
	}
	
	// Assesseur sur l'attribut name
	public String getName() {
		return this.name;
	}
	
	// Renvoi l'objet sous la forme de string
	public String toString(){
		String string = "Profile :" + id + "\n";
		string += "Name :" + name + "\n";
		string += "-------Properties--------"  + "\n";
		
		for(int i = 0; i < properties.size(); i++){
			Property p = properties.get(i);
			string += "Entity :" + p.getName() + " : " +  p.getType() +  "\n";
		}
		
		return string;
	}
	
	// Rempli la liste de propriétés par les valeurs en praramétre 
	public void setValues(List<String> values){
		valeurs = new ArrayList<String>();
		if(values.size() > 0){
			Log.i("ji", values.get(0) + "");
			this.setId(values.get(0));
			for(int i = 1; i < values.size(); i++){
				this.valeurs.add(values.get(i));
			}
		}
	}
	
	// Récupére une valeur spécifique
	public String getValue(int i){
		return this.valeurs.get(i);
	}
	
	// Récupére un type de propriété spécifique
	public Property.Type getPropertyType(int i){
		return this.properties.get(i).getType();
	}
	
	// Récupére le nom de propriété spécifique
	public String getPropertyName(int i){
		return this.properties.get(i).getName();
	}
}
