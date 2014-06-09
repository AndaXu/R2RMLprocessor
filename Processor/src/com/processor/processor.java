package com.processor;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hp.hpl.jena.*;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;


public class processor {
	private Connection mCon;
	private File mMapDoc;
	
	public processor (Connection con, File md){
		mCon = con;
		mMapDoc = md;
	}

	public List<String> DoR2RMap() {
		
		List<String> listRDF = new ArrayList<String>();
		InputStream in = FileManager.get().open(mMapDoc.getPath());
		List<TriplesMap> triplesMaps = new ArrayList<TriplesMap>();
		//TriplesMap tp = new TriplesMap();
		TriplesMap tp;
		
		Model dModel = ModelFactory.createDefaultModel();
		dModel.read(in, "", "TTL");
		OntModel oModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, dModel);
		StmtIterator iter = oModel.listStatements();
		
		/** while (iter.hasNext())   
	        {  
	            Statement stmt = iter.nextStatement(); // get next statement  
	            Resource subject = stmt.getSubject(); // get the subject  
	            Property predicate = stmt.getPredicate(); // get the predicate  
	            RDFNode object = stmt.getObject(); // get the object  
	            	            
	            System.out.print("Ö÷Óï " + subject);    
	            System.out.print(" Î½Óï " + predicate);         
	            if (object instanceof Resource)   
	            {  
	                System.out.print(" ±öÓï " + object);  
	            }  
	            else {// object is a literal  
	                System.out.print("±öÓï \"" + object.toString() + "\"");  
	            }  
	            System.out.println(" .");   
	            
	        } 
		**/
		//
		Map<String, String> prefixMap = oModel.getNsPrefixMap();
		Set<Entry<String, String>> ss = prefixMap.entrySet();
		Iterator<Entry<String, String>> it = ss.iterator();

		while (it.hasNext()){
			Entry<String, String> en = it.next();
			System.out.println("Key : " + en.getKey() + "-----Value : " + en.getValue());
			
		}
		
		
		ArrayList<Resource> rootSubjects = new ArrayList<Resource>();
		while (iter.hasNext()){
			Statement stmt = iter.next();
			Property pd = stmt.getPredicate();
			if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#type".equals(pd.toString())) {
				rootSubjects.add(stmt.getSubject());
			}
		}/*********/
		
		for (int i = 0; i < rootSubjects.size(); i++){
			
			Resource root = rootSubjects.get(i);
			StmtIterator itNew = oModel.listStatements(root, null, (RDFNode)null);
			tp = new TriplesMap();
			tp.predicateObjectMap = new ArrayList<PredicateObjectMap>();
			while(itNew.hasNext()){
				
				Statement st = itNew.next();
				Property pd = st.getPredicate();
				System.out.println(st.getSubject().toString()+"   "+st.getPredicate().toString()+"   "+st.getObject().toString());
				String name = pd.toString().substring(pd.toString().lastIndexOf("#")+1);
				if(name.equals("type")){
					String strSub = st.getSubject().toString();
					int index = strSub.lastIndexOf("/");
					if(index > 0){
						tp.Name = strSub.substring(index+1);
					}
					
				}
				else if (name.equals("logicalTable")){
					RDFNode tmpObj1 = st.getObject();
					StmtIterator tmpItObj1 = oModel.listStatements(tmpObj1.asResource(), null, (RDFNode)null);
					Statement lt = tmpItObj1.next();
					//tp.logicalTable = lt.getObject().toString();
					boolean flag = true;
					while(flag){
						
						Property pLogiTable = lt.getPredicate();
						String typeOfLogiTable = pLogiTable.toString().substring(pLogiTable.toString().lastIndexOf("#")+1);
						if(typeOfLogiTable.equals("tableName")){
							tp.logicalTable = "T#" + lt.getObject().toString();
							flag = false;
						}
						else if(typeOfLogiTable.equals("sqlQuery")){
							tp.logicalTable = "R#" + lt.getObject().toString();
							flag = false;
						}
						else{
							lt =  tmpItObj1.next();
						}
					}
//					if(lt.getObject().isResource()){
//						tp.logicalTable = lt.getObject().asResource();
//					}
//					else {
//						tp.logicalTable = tmpObj1.asResource();
//					}
					//tp.logicalTable=tmpItObj1.next().getResource();
				}
				else if (name.equals("subjectMap")) {
					RDFNode tmpObj2 = st.getObject();
					StmtIterator tmpItObj2 = oModel.listStatements(tmpObj2.asResource(), null, (RDFNode)null);
					tp.subjectMap = new SubjectMap();
					while(tmpItObj2.hasNext()){
						Statement tmpSub = tmpItObj2.next();
						Property subPredicate = tmpSub.getPredicate();
						String strPredicate = subPredicate.toString();
						//String preName = strPredicate.substring(strPredicate.lastIndexOf("#")+1);
						int mark = strPredicate.lastIndexOf("#");
						if (mark == -1){
							mark = strPredicate.lastIndexOf(":");
						}
						String preName = strPredicate.substring(mark+1);
						//tp.subjectMap = new SubjectMap();
						if (preName.equals("termType")){
							tp.subjectMap.termType = tmpSub.getObject().asResource();
						}
						else if (preName.equals("template")){
							tp.subjectMap.ve = veType.template;
							tp.subjectMap.template = tmpSub.getObject().toString();
							if(tmpSub.getObject().isResource()){
								tp.subjectMap.classs=tmpSub.getObject().asResource();
							}
							else {
								tp.subjectMap.classs = tmpObj2.asResource();
							}
							
						}
						else if (preName.equals("column")){
							tp.subjectMap.ve = veType.column;
							tp.subjectMap.columnName = tmpSub.getObject().toString();
						}
						else if (preName.equals("class")){
							String valueCla = "";
							valueCla = "<" + prefixMap.get("rdf") + "type>\t<" + tmpSub.getObject().toString() + ">";
							tp.subjectMap.hasClass = true;
							if(tp.subjectMap.valueClass == null){
								tp.subjectMap.valueClass = new  ArrayList<String>();
							}
							tp.subjectMap.valueClass.add(valueCla);
						}
						else if (preName.equals("constant")){
							tp.subjectMap.ve = veType.constant;
							tp.subjectMap.constantValueStr = tmpSub.getObject().toString();
						}
						else if(preName.equals("graphMap")){
							RDFNode tmpObj3 = tmpSub.getObject();
							StmtIterator tmpItGM = oModel.listStatements(tmpObj3.asResource(), null, (RDFNode)null);
							tp.subjectMap.graphMap = new GraphMap();
							while(tmpItGM.hasNext()){
								Statement tmpGM = tmpItGM.next();
								Property subPredGM = tmpGM.getPredicate();
								String strPredGM = subPredGM.toString();
								int mark2 = strPredGM.lastIndexOf("#");
								if (mark2 == -1){
									mark2 = strPredGM.lastIndexOf(":");
								}
								String preName2 = strPredGM.substring(mark2+1);
								if(preName2.equals("constant")){
									tp.subjectMap.graphMap.ve = veType.constant;
									tp.subjectMap.graphMap.constantValueStr = tmpGM.getObject().toString();
								}
								else if (preName2.equals("column")){
									String valueCol = tmpGM.getObject().toString();
									tp.subjectMap.graphMap.ve = veType.column;
									tp.subjectMap.graphMap.columnName = valueCol;
								}
								else if (preName2.equals("template")){
									tp.subjectMap.graphMap.ve = veType.template;
									tp.subjectMap.graphMap.template = tmpGM.getObject().toString();
								}
							}
							
							//tp.subjectMap.graphMap
						}
						else if(preName.equals("graph")){
							tp.subjectMap.graphMap = new GraphMap();
							tp.subjectMap.graphMap.ve = veType.constant;
							tp.subjectMap.graphMap.constantValueStr = tmpSub.getObject().toString();
						}
						
					}
				}
				else if (name.equals("predicateObjectMap") ){
					//PredicateMap tmpPM = new PredicateMap();
					PredicateObjectMap tmpPOM = new PredicateObjectMap();
					//tmpPM.predicate = new ArrayList<Resource>();
					tmpPOM.predicateMap = new ArrayList<PredicateMap>();
					//tp.predicateObjectMap = new ArrayList<PredicateObjectMap>();
					
					RDFNode tmpObj3 = st.getObject();
					StmtIterator tmpItObj3 = oModel.listStatements(tmpObj3.asResource(), null, (RDFNode)null);
					while(tmpItObj3.hasNext()){
						Statement tmpSub = tmpItObj3.next();
						Property subPredicate = tmpSub.getPredicate();
						String strPredicate = subPredicate.toString();
						String preName = strPredicate.substring(strPredicate.lastIndexOf("#")+1);
						if (preName.equals("predicate")){
							//List<Resource> tmpPred = new ArrayList<Resource>();
							//tmpPred.add(tmpSub.getObject().asResource());
							//PredicateMap tmpPM = new PredicateMap();
							//PredicateObjectMap tmpPOM = new PredicateObjectMap();
							//tp.predicateObjectMap.add(tmpPOM);
							
							//constantValue 
							//tmpPM.predicate = new ArrayList<Resource>();
							PredicateMap tmpPM = new PredicateMap();
							tmpPM.predicate = new ArrayList<Resource>();
							tmpPM.predicate.add(tmpSub.getObject().asResource());
							tmpPM.ve = veType.constant;
							tmpPM.constantValue = tmpSub.getObject().asResource();
							
							//tmpPOM.predicateMap = new ArrayList<PredicateMap>();
							tmpPOM.predicateMap.add(tmpPM);
							
							
						}
						else if (preName.equals("predicateMap")){
							RDFNode tmpObj4 = tmpSub.getObject();
							StmtIterator tmpItObj4 = oModel.listStatements(tmpObj4.asResource(), null, (RDFNode)null);
							while(tmpItObj4.hasNext()){
								PredicateMap tmpPM = new PredicateMap();
								tmpPM.predicate = new ArrayList<Resource>();
								Statement tmpSt = tmpItObj4.next();
								Property tmpPd= tmpSt.getPredicate();
								String strPd = tmpPd.toString();
								String preName2 = strPd.substring(strPd.lastIndexOf("#")+1);
								if (preName2.equals("termType")){
									//tmpPOM.objectMap.termType = tmpSt.getObject().asResource();	
								}
								else if (preName2.equals("template")){
									//tp.subjectMap.classs=tmpSub.getObject().asResource();
									/*
									tmpPOM.objectMap.ve = veType.template;
									tmpPOM.objectMap.template = tmpSt.getObject().toString();
									if(tmpSt.getObject().isResource()){
										tmpPOM.objectMap.object = tmpSt.getObject().asResource();
									}
									else {
										tmpPOM.objectMap.object = tmpObj4.asResource();
									}
									*/
								}
								else if (preName2.equals("column")){
									/*tmpPOM.objectMap.ve = veType.column;
									tmpPOM.objectMap.columnName = tmpSt.getObject().toString();
									RDFNode finalObj = tmpSt.getObject();
									if(finalObj.isResource()){
										tmpPOM.objectMap.object = tmpSt.getObject().asResource();
									}
									else {
										tmpPOM.objectMap.object = tmpObj4.asResource();
									}
									*/
									
								}
								else if (preName2.equals("constant")){
									tmpPM.predicate.add(tmpSt.getObject().asResource());
									tmpPM.ve = veType.constant;
									tmpPM.constantValue = tmpSt.getObject().asResource();
									tmpPOM.predicateMap.add(tmpPM);
								}
							}
						}
						else if (preName.equals("objectMap")){
							RDFNode tmpObj4 = tmpSub.getObject();
							StmtIterator tmpItObj4 = oModel.listStatements(tmpObj4.asResource(), null, (RDFNode)null);
							while(tmpItObj4.hasNext()){
								Statement tmpSt = tmpItObj4.next();
								//tmpPOM.objectMap = new ObjectMap();
								Property tmpPd= tmpSt.getPredicate();
								String strPd = tmpPd.toString();
								String preName2 = strPd.substring(strPd.lastIndexOf("#")+1);
								if (preName2.equals("termType")){
									tmpPOM.objectMap = new ObjectMap();
									tmpPOM.objectMap.termType = tmpSt.getObject().asResource();	
								}
								else if (preName2.equals("template")){
									//tp.subjectMap.classs=tmpSub.getObject().asResource();
									tmpPOM.objectMap = new ObjectMap();
									tmpPOM.objectMap.ve = veType.template;
									tmpPOM.objectMap.template = tmpSt.getObject().toString();
									if(tmpSt.getObject().isResource()){
										tmpPOM.objectMap.object = tmpSt.getObject().asResource();
									}
									else {
										tmpPOM.objectMap.object = tmpObj4.asResource();
									}
									
								}
								else if (preName2.equals("column")){
									//tmpPOM.objectMap = new ObjectMap();
									if(tmpPOM.objectMap == null){
										tmpPOM.objectMap = new ObjectMap();
									}
									tmpPOM.objectMap.ve = veType.column;
									tmpPOM.objectMap.columnName = tmpSt.getObject().toString();
									RDFNode finalObj = tmpSt.getObject();
									if(finalObj.isResource()){
										tmpPOM.objectMap.object = tmpSt.getObject().asResource();
									}
									else {
										tmpPOM.objectMap.object = tmpObj4.asResource();
									}
									
									
								}
								else if (preName2.equals("constant")){
									tmpPOM.objectMap = new ObjectMap();
									tmpPOM.objectMap.ve = veType.constant;
									tmpPOM.objectMap.isRefObjectMap = false;
									tmpPOM.objectMap.constantValueStr = tmpSt.getObject().toString();
								}
								else if (preName2.equals("language")){//case 15a
									if(tmpPOM.objectMap == null){
										tmpPOM.objectMap = new ObjectMap();
									}
									tmpPOM.objectMap.language =  tmpSt.getObject().toString();
								}
								else if (preName2.equals("parentTriplesMap")){
									// ref object map
									tmpPOM.objectMap = new ObjectMap();
									String strRefName = tmpSt.getObject().toString();
									tmpPOM.objectMap.ve = veType.constant;
									tmpPOM.objectMap.isRefObjectMap = true;
									tmpPOM.objectMap.refObjectName = strRefName.substring(strRefName.lastIndexOf("/")+1);
								}
							}
						}
						else if (preName.equals("object")){
							tmpPOM.objectMap = new ObjectMap();
							tmpPOM.objectMap.ve = veType.constant;
							tmpPOM.objectMap.isRefObjectMap = false;
							try{
								tmpPOM.objectMap.constantValue =  tmpSub.getObject().asResource();
							}catch(ResourceRequiredException e){
								System.out.println(e.toString());
							}
							tmpPOM.objectMap.constantValueStr = tmpSub.getObject().toString();
						}
					}
					tp.predicateObjectMap.add(tmpPOM);
				}// end of predicateObjectMap				
				else {
					
				}
			}
			triplesMaps.add(tp);
		//end of FOR
		}
		// dealing with each triples map
		//listRDF = new ArrayList<String>();
		
		/*
		Iterator<TriplesMap> tmIt = triplesMaps.iterator();
		while(tmIt.hasNext()){
			TriplesMap tmpTM = tmIt.next();
				
			List<String> one = DoTriplesMap(tmpTM, triplesMaps);
				
			listRDF.addAll(one);
			
		}
		*/
		boolean flag = true;
		while(flag){
			Iterator<TriplesMap> tmIt1 = triplesMaps.iterator();
			if (tmIt1.hasNext()){
				TriplesMap tmpTM1 = tmIt1.next();
				if(tmpTM1 != null){//pick next, deal element
					if(tmpTM1.Name.startsWith("RefObjectMap")){
						triplesMaps.remove(tmpTM1);
						continue;
					}
					List<String> one = DoTriplesMap(tmpTM1, triplesMaps);
					
					listRDF.addAll(one);
					triplesMaps.remove(tmpTM1);
					//tmIt1.remove();
				}
				else{//element has deleted, continue to deal next
					continue;
				}
			}
			else{//no elements, end loop
				flag = false;
			}
		}
		
		//listRDF = DoTriplesMap(triplesMaps);
		//2014-04-03
		List<String> tempRS = new ArrayList<String>();
		Iterator<String> tempIT = listRDF.iterator();
		while(tempIT.hasNext()){
			String ele = tempIT.next();
			if(tempRS.contains(ele)){
				continue;
			}
			else
			{
				tempRS.add(ele);
			}
		}
		
		//return listRDF;
		return tempRS;
		/*******   Separate the statements into different parts by root subject.
		List<RDFNode> tempObjs = new ArrayList<RDFNode>();
		List<Statement> tempStatement = new ArrayList<Statement>();
		Map<Resource, List<Statement>> groupOfResources = new Map<String, List<Statement>>();
		for (int i = 0; i < rootSubjects.size(); i++){
			Resource rootSubject = rootSubjects.get(i);
			//Resource tempSubject = root;
			StmtIterator itNew = oModel.listStatements(rootSubject, null, (RDFNode)null);
			while(itNew.hasNext()){
				Statement curSt = itNew.next();
				tempStatement.add(curSt);
				if(!curSt.getSubject().toString().startsWith("http://")){
					tempObjs.add(curSt.getSubject());
				}
			}
			if(tempObjs.size() > 0){
				List<Statement> tempResulte = GetStatements(tempObjs, oModel);
				tempStatement.addAll(tempResulte);
			}
			groupOfResources.put(rootSubject, tempStatement);
		}
		********/
		
		//InputStream in = FileManager.get().open(mMapDoc.getPath());
		//model.read(in, "", "TTL");
		//StmtIterator iter = model.listStatements(); 
		/****** DEBUG
		 while (iter.hasNext())   
	        {  
	            Statement stmt = iter.nextStatement(); // get next statement  
	            Resource subject = stmt.getSubject(); // get the subject  
	            Property predicate = stmt.getPredicate(); // get the predicate  
	            RDFNode object = stmt.getObject(); // get the object  
	            
	           
	            String subjectName = subject.toString();
	            String predicateName = predicate.toString();
	            String objectName = object.toString();
	            
	            String[] pn = new String[2];
	            pn = GetPrefixAndName(prefixMap.values(), subjectName);
	            
	            
	            if(pn == null){
	            	System.out.println("subject is : " + subjectName);
	            }
	            else{
	            	System.out.println("prefix is : " + pn[0] + "---name is : " +pn[1]);
	            }
	           
	            		
	            Resource tttt= stmt.getResource();
	            System.out.println("******:  " + tttt);
	  
	            
	            String subjectS = stmt.getSubject().toString(); // get the subject  
	            String predicateS = stmt.getPredicate().toString(); // get the predicate  
	            RDFNode objectS = stmt.getObject(); // get the object  
	            
	            System.out.print("Ö÷Óï " + subject);    
	            System.out.print(" Î½Óï " + predicate);         
	            if (object instanceof Resource)   
	            {  
	                System.out.print(" ±öÓï " + object);  
	            }  
	            else {// object is a literal  
	                System.out.print("±öÓï \"" + object.toString() + "\"");  
	            }  
	            System.out.println(" .");   
	            
	        } 
		 */
		 
		 //TODO£º deal with each elements in triplesMaps
		 
		 //TODO write the result to the file.
		 
	}
	
	//private List<String> DoTriplesMap(TriplesMap tm){
	private List<String> DoTriplesMap(TriplesMap tm, List<TriplesMap> tp){
		/*
		Iterator<TriplesMap> tmIt = triplesMaps.iterator();
		while(tmIt.hasNext()){
			TriplesMap tmpTM = tmIt.next();
				
			List<String> one = DoTriplesMap(tmpTM);
				
			listRDF.addAll(one);
			
		}*/
		//List<String> finalrs = new ArrayList<String>();
		//Iterator<TriplesMap> tmIt = tp.iterator();
		//while(tmIt.hasNext()){
			//TriplesMap tm = tmIt.next();
		
		List<String> result = new ArrayList<String>();
		System.out.print("Current tp is : " + tm.Name + "\n");
		//Resource logicalTable = tm.logicalTable;
		String logicalTable = tm.logicalTable;
		//SubjectMap subMap = tm.subjectMap;
		String sqlQuery = GenerateQuery(logicalTable);
		if (sqlQuery.startsWith("R#")){
			result.add(sqlQuery.substring(2));
			return result;
		}
		else{
		try {
			java.sql.Statement sqlSt = mCon.createStatement();
			sqlSt.execute(sqlQuery);
			ResultSet rs = sqlSt.getResultSet();
			//If the table is Empty
			int count =0;
			//rs.findColumn(columnLabel)
			while(rs.next()){
				
				//get subject
				//Resource sub = tm.subjectMap.classs;
				//String strSub = sub.toString();
				
				String strSub = "";
				String valueSub = "";
				String valueGM = "";
				if(tm.subjectMap != null){
				switch (tm.subjectMap.ve){
				case template :
					
					if(strSub == ""){
						strSub = tm.subjectMap.template;
						valueSub = strSub;
						int index = 0;
						while (strSub.indexOf("{", index) >= 0){
							int begin = strSub.indexOf("{", index);
							int end = strSub.indexOf("}", index);
							String tmp = "";
							if(end+1 < strSub.length()){
								tmp = strSub.substring(begin, end+1);
							}
							else {
								tmp = strSub.substring(begin);
							}
							String tmp2 = TrimString2(TrimString1(tmp));
							//strSql = strSql + tmp + ", ";
							index = end+1;
							//int indCol = rs.findColumn(tmp2);
							//System.out.print("index of column "+tmp2+" is "+indCol+"\n");
							String tmp3 = rs.getString(tmp2);
							if(tmp3 == null){
								tmp3="null";
							}
							valueSub = valueSub.replace(tmp, tmp3);
							//valueSub = valueSub + rs.getString(tmp);//subject value
						}
					}
					break;
				case constant :
					//valueSub = sub.toString();
					//valueSub = tm.subjectMap.constantValue.toString();
					valueSub = tm.subjectMap.constantValueStr;
					break;
				case column :
					//strSub = sub.toString();
					strSub = tm.subjectMap.columnName;
					String tmpC = TrimString2(strSub);
					//valueSub = valueSub + rs.getString(tmpC);
					valueSub = valueSub +GetSqlString(tmpC, logicalTable, rs);
					break;
					default:
						break;
						
					
				}
				//2014-04-02
				if(valueSub.startsWith("http:")){
					valueSub = "<" + valueSub + ">";
				}
				else{
					valueSub = "_:" + valueSub;
				}
				//2014-04-14
				/*if(!valueSub.startsWith("http:")){
					valueSub = "_:" + valueSub;
				}*/
				if(tm.subjectMap.hasClass){
					List<String> tmpStr = tm.subjectMap.valueClass;
					for (int i=0; i<tmpStr.size(); i++){
						//result.add("<"+valueSub+">" + "\t\t" + tmpStr.get(i) + "\n");
						result.add(""+valueSub+"" + "\t\t" + tmpStr.get(i) + "\n");//2014-03-24
					}
						
					//result.add("<"+valueSub+">" + "\t\t" + tm.subjectMap.valueClass + "\n");
				}
//				if(tm.subjectMap.termType != null){
//					String tt = tm.subjectMap.termType.toString();
//					if (tt.indexOf("BlankNode") > 0){
//						valueSub = "_:" + valueSub;
//					}
//				}
				
				// Graph Map
				//String valueGM = "";
				if (tm.subjectMap.graphMap != null){
					switch(tm.subjectMap.graphMap.ve){
					case constant:
						valueGM = tm.subjectMap.graphMap.constantValueStr;
						break;
					case column:
						String valueCol = tm.subjectMap.graphMap.columnName;
						String tmpO = TrimString2(valueCol);
						valueGM = GetSqlString(tmpO, logicalTable, rs);
						break;
					case template:
						String valueTemp = "";
						if(valueTemp == ""){
							valueTemp = tm.subjectMap.graphMap.template;
							valueGM = valueTemp;
							int index = 0;
							while (valueTemp.indexOf("{", index) >= 0){
								int begin = valueTemp.indexOf("{", index);
								int end = valueTemp.indexOf("}", index);
								String tmp = "";
								if(end+1 < valueTemp.length()){
									tmp = valueTemp.substring(begin, end+1);
								}
								else {
									tmp = valueTemp.substring(begin);
								}
								String tmp2 = TrimString2(TrimString1(tmp));
								//strSql = strSql + tmp + ", ";
								index = end+1;
								String tmp3 = rs.getString(tmp2);
								valueGM = valueGM.replace(tmp, tmp3);
								//valueSub = valueSub + rs.getString(tmp);//subject value
							}
						}
						break;
						default:
							break;
					}
				}
				}//end IF subject is not NULL
				
				//predicate & object
				//List<String> valuePre = new ArrayList<String>();
				String valueObj = "";
				//ref obj
				List<String> valueObjs = new ArrayList<String>();
				List<String> valueObjRefs = new ArrayList<String>();
				//predicate object 
				List<PredicateObjectMap> poj = tm.predicateObjectMap;
				for(int i = 0; i < poj.size(); i++){
					PredicateObjectMap po = poj.get(i);
					//PredicateMap pm = po.predicateMap.get(0);
					List<PredicateMap> pmm = po.predicateMap;
					
					List<String> valuePre = new ArrayList<String>();
					
					for (int j = 0; j < pmm.size(); j++){
						PredicateMap pm = pmm.get(j);
						switch (pm.ve){
						case constant :
							valuePre.add(pm.constantValue.toString());//pm.predicate.get(0).toString();//predicate value
							break;
							
						case column :
						case template :
						default:
							break;
						}
					}
					
					
					
					//Resource obj = po.objectMap.object;
					//valueObj = rs.getString(obj.toString());//object value
					ObjectMap obj = po.objectMap;
					String strObj = "";
					switch (obj.ve){
					case constant :
						if(obj.isRefObjectMap){
							valueObj = obj.refObjectName;
							Iterator<TriplesMap> refIt = tp.iterator();
							TriplesMap ref = null;
							while(refIt.hasNext()){
								TriplesMap tmp = refIt.next();
								if (tmp.Name.equals(valueObj)){
									ref = tmp;
									refIt.remove();
								}
							}
							if(ref != null){
								valueObj = "";
								List<String> refRs = DoTriplesMap(ref, tp);
								Iterator<String> subIt = refRs.iterator();
								List<String> refSub = new ArrayList<String>();
								while(subIt.hasNext()){
									String tmp = subIt.next();
									int inTab = tmp.indexOf("\t");
									String tmpSub = tmp.substring(0, inTab);
									
									if(!refSub.contains(tmpSub)){
										refSub.add(tmpSub);
									}
								}
								valueObjs = refSub;
								valueObjRefs = refRs;
							}
						}
						else{
							valueObj = obj.constantValueStr.toString();
						}
						//valueObj = obj.constantValueStr.toString();
						break;
					case column :
						//valueObj = rs.getString(TrimString2(obj.columnName));
						String tmpO = TrimString2(obj.columnName);
						valueObj = GetSqlString(tmpO, logicalTable, rs);
						if(obj.language != null){
							valueObj = valueObj + " @ " + obj.language;
						}
						break;
					case template :
						
						if(strObj == ""){
							strObj = po.objectMap.template;
							valueObj = strObj;
							int index = 0;
							while (strObj.indexOf("{", index) >= 0){
								int begin = strObj.indexOf("{", index);
								int end = strObj.indexOf("}", index);
								String tmp = "";
								if(end+1 < strObj.length()){
									tmp = strObj.substring(begin, end+1);
								}
								else {
									tmp = strObj.substring(begin);
								}
								String tmp2 = TrimString2(TrimString1(tmp));
								index = end+1;
								String tmp3 = rs.getString(tmp2);
								valueObj = valueObj.replace(tmp, tmp3);
							}
						}
						break;
					default:
						break;
					}//end object value
					
					if(valueObj == null){
						continue;
					}
					
					if(!valueObj.equals("")){
						//add to list of triples
						for(int k=0; k<valuePre.size(); k++){
							//result.add("<"+valueSub+">" + "\t" + "<"+valuePre.get(k)+">" + "\t" + "<"+valueObj+">" + "\t" + "<"+valueGM+">" + "\n");
							
							/*if(valueObj.startsWith("http:")){//2014-04-05
								valueObj = "<" + valueObj + ">";
							}
							else{
								valueObj = "\"" + valueObj + "\"";
							}*/
							
							
							result.add(valueSub + "\t"  + "<"+valuePre.get(k) +">" +  "\t" +FormatString(valueObj) + "\t" +FormatString(valueGM) + "\n");//2014-03-24
						}
					}
					else{
						//ref objs
						//List<String> valueObjs = new ArrayList<String>();
						//List<String> valueObjRefs = new ArrayList<String>();
						Iterator<String> tmpIt = valueObjs.iterator();
						while(tmpIt.hasNext()){
							String tmpObj = tmpIt.next();
							//result.add("<"+valueSub+">" + "\t" + "<"+valuePre.get(0)+">" + "\t" + tmpObj + "\t" + "<"+valueGM+">" + "\n");
							/*if(tmpObj.startsWith("http:")){//2014-04-05
								tmpObj = "<" + tmpObj + ">";
							}
							else{
								tmpObj = "\"" + tmpObj + "\"";
							}*/
							
							result.add(valueSub + "\t"  + "<"+valuePre.get(0)+">" + "\t" + FormatString(tmpObj) + "\t" +FormatString(valueGM)+ "\n");//2014-03-24
							Iterator<String> tmpIt2 = valueObjRefs.iterator();
							while(tmpIt2.hasNext()){
								String tmpStr = tmpIt2.next();
								if(tmpStr.startsWith(tmpObj)){
									result.add(tmpStr);
								}
							}
						}
					}
					
					//result.add("<"+valueSub+">" + "\t" + "<"+valuePre+">" + "\t" + "<"+valueObj+">" + "\t" + "<"+valueGM+">" + "\n");
				}//end of predicate&object
					count = count + 1;
				}
					if(count == 0){
						result.add("# empty graph\n");
					}
				} catch (SQLException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
				}
				return result;
			}//end else
			//finalrs.addAll(result);
		//}//end while
		//return finalrs;
	}
	
	//private String GenerateQuery(Resource lt){
	private String GenerateQuery(String lt){
		String rs = "";
		System.out.println("tmp sql query is : " + lt + "\n");
		
		String type = lt.substring(0,1);
		String tmp = lt.substring(2);
		int start = -1;
		boolean flag = true;
		while(flag){
			start = start + 1;
			String cur = tmp.substring(start,start+1);
			if(!cur.equals("\r") && !cur.equals("\n") && !cur.equals("\t") && !cur.equals(" ")){
				flag = false;
			}
		}
		tmp = tmp.substring(start);
		//tmp = tmp.toLowerCase();
		//tmp = tmp.replace("\"", "");
		tmp = tmp.replace("\r", "");
		tmp = tmp.replace("\n", "");
		tmp = tmp.replace("\t", "");
		if(type.equals("T")){
			if(tmp.startsWith("\"") || tmp.endsWith("\"")){
				String tableName = tmp.substring(1, tmp.length()-1);
				int index = -1;
				index = tableName.indexOf(" ");
				if(index > 0){
					tableName = "`" + tableName + "`";
				}
				rs = "select * from " + tableName;
			}
			else {
				rs = "select * from " + tmp;
			}
		}
		else if (type.equals("R")){
			if(tmp.startsWith("select")||tmp.startsWith("Select")||tmp.startsWith("SELECT")){
				int ias = 0;
				ias = tmp.indexOf(" AS ");
				if (ias > 0){
					boolean tag = true;
					int begin, end, tail;
					String org, rep = "";
					String comb = "";
					String[] sets;
					while(tag){
						begin = tmp.indexOf("(");
						end = tmp.indexOf(")");
						tail = tmp.indexOf(",", ias+4);
						if(tail < 0){
							tail = tmp.indexOf(" ", ias+4);
						}
						
						if(begin>0){
						org = tmp.substring(begin, tail);
						comb = tmp.substring(begin+1, end);
						
						if(comb.startsWith("CASE")){// case 14d
							ias = tmp.indexOf(" AS ", ias+4);
							if(ias < 0 ){
								tag = false;
							}
						}
						else{
						sets = comb.split("\\|\\|");
						for(int i=0; i< sets.length; i++){
							String set = sets[i];
							int iQout = set.indexOf("\"");
							int iQout2 = set.indexOf("\'");
							if(iQout >= 0){
								rep = rep + set.substring(iQout+1, set.length()-1) + ",";
							}
							else if (iQout2 >= 0){
								
							}
							else{
								rep = rep + set + ",";
							}
						}
						if(rep.endsWith(",")){
							rep = rep.substring(0, rep.length()-1);
						}
						tmp = tmp.replace(org, rep);
						
						ias = tmp.indexOf(" AS ");
						if(ias < 0 ){
							tag = false;
						}
						}//end else
						}//end if(begin)
						else{
							ias = tmp.indexOf(" AS ", ias+4);
							if(ias < 0 ){
								tag = false;
							}
						}
					}
				}
				tmp = tmp.replace("\"", " ");
				rs = tmp;
			}
			else {
				rs = lt;
			}

		}
		
		//String tmp = lt.toString();
		/*String tmp = lt;		
		if(tmp.startsWith("select")||tmp.startsWith("Select")){
			rs = tmp;
		}
		else {
			//table name 
			if(tmp.startsWith("\"") || tmp.endsWith("\"")){
				rs = "select * from " + tmp.substring(1, tmp.length()-1);
			}
			else {
				rs = "select * from " + tmp;
			}
			
		}*/
		System.out.println("sql query is : " + rs + "\n");
		return rs;
	}
	private String TrimString2 (String s){
		String rs = "";
		rs = s;
		if(s.startsWith("\"") || s.endsWith("\"")){
			rs = s.substring(1, s.length()-1);
			int index = -1;
			index = rs.indexOf(" ");
			if(index > 0){
				rs = "[" + rs + "]";
			}
		}
		else {
			rs = s;
		}
		return rs;
	}
	private String TrimString1 (String s){
		String rs = "";
		rs = s;
		if(s.startsWith("{") || s.endsWith("}")){
			rs = s.substring(1, s.length()-1);
		}
		else {
			rs = s;
		}
		return rs;
	}
	private String FormatString (String org){
		String rs = org;
		if(org.startsWith("http:")){//2014-04-05
			rs = "<" + org + ">";
			rs = rs.replace(" ", "20%");
		}
		else if(org.equals("")){
			
		}
		else{
			rs = "\"" + org + "\"";
		}
		return rs;
	}
	private String GetSqlString (String column, String query, ResultSet results){
		String rs = "";
		int index1 = query.indexOf(" AS "+column);
		if(index1<0){
			index1 = query.indexOf(" AS \""+column);//case 14a
		}
		//04b
		String str2 = "";
		if(index1 > 0){
			String str3 =  query.substring(index1+4,index1+5);//14a
			if(str3.startsWith("\"")){
				str2 = query.substring(index1+4+column.length()+2,index1+4+column.length()+3);
			}
			else{
				str2 = query.substring(index1+4+column.length(),index1+4+column.length()+1);
			}
			
		}
			
		if(index1 > 0 && (str2.equals(",")||str2.equals("\r")||str2.equals(" ")||str2.equals("\t")||str2.equals("\n"))){
			
			String str1 = query.substring(index1-1,index1);
			if(str1.equals(")")){
				boolean flag = true;
				int index2 = 0;
				int index3 = 0;
				while(flag){
					index3 = query.indexOf("(", index3+1);
					if(index3 >= index1 || index3 < 0){
						flag = false;
					}
					else{
						index2 = index3;
					}
				}
				index3 = query.indexOf(")");
				String source = query.substring(index2+1,index3);
				String[] set = source.split("\\|\\|");
				for(int i=0; i< set.length; i++){
					String tmp = set[i];
					int k = tmp.indexOf("\'");
					//if(tmp.startsWith("\'")){
					if(k >=0 ){
						int ii = tmp.lastIndexOf("\'");
						String name = tmp.substring(k+1, ii);
						rs = rs + name;
					}
					else{
						try {
							int ii = tmp.indexOf("\"");
							int iii = tmp.lastIndexOf("\"");
							if (ii>=0 && iii >ii){
								String name = tmp.substring(ii+1, iii);
								//rs = rs + results.getString(name);
								//case 16c 16d
								int index = results.findColumn(name);
								ResultSetMetaData md = results.getMetaData();
								int ty = md.getColumnType(index);
								switch(ty){
								case Types.BIT :
									byte bty = results.getByte(index);
									if (bty == 0){
										rs = rs + "false";
									}
									else {
										rs = rs + "ture";
									}
									break;
								case Types.TIMESTAMP:
									rs = rs + results.getTimestamp(index).toString();
									break;
									default:
										rs = rs + results.getString(name);
									 
								}
							}
							else{//02h
								String name = tmp.trim();
								//case 16c 16d
								int index = results.findColumn(name);
								ResultSetMetaData md = results.getMetaData();
								int ty = md.getColumnType(index);
								switch(ty){
								case Types.BIT :
									byte bty = results.getByte(index);
									if (bty == 0){
										rs = rs + "false";
									}
									else {
										rs = rs + "ture";
									}
									break;
								case Types.TIMESTAMP:
									rs = rs + results.getTimestamp(index).toString();
									break;
									default:
										rs = rs + results.getString(name);
									 
								}
							}
						
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			else{
				try {
					//rs = results.getString(column);
					//case 16c 16d
					int index = results.findColumn(column);
					ResultSetMetaData md = results.getMetaData();
					int ty = md.getColumnType(index);
					switch(ty){
					case Types.BIT :
						byte bty = results.getByte(index);
						if (bty == 0){
							rs = rs + "false";
						}
						else {
							rs = rs + "ture";
						}
						break;
					case Types.TIMESTAMP:
						rs = rs + results.getTimestamp(index).toString();
						break;
						default:
							rs = results.getString(column);
						 
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			try {
				//rs = results.getString(column);
				//case 16c 16d
				int index = results.findColumn(column);
				ResultSetMetaData md = results.getMetaData();
				int ty = md.getColumnType(index);
				switch(ty){
				case Types.BIT :
					byte bty = results.getByte(index);
					if (bty == 0){
						rs = rs + "false";
					}
					else {
						rs = rs + "ture";
					}
					break;
				case Types.TIMESTAMP:
					rs = rs + results.getTimestamp(index).toString();
					break;
					default:
						rs = results.getString(column);
					 
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rs;
	}
	
	
	/**
	private String[] GetPrefixAndName(Collection<String> prefixes, String sour)
	{
		Iterator<String>  it = prefixes.iterator();
		//Entry<String, String> rs = new Entry<String, String>(null, null);
		String[] rs = new String[2];
		
		while(it.hasNext()){
			
			String temp = it.next();
			if(sour.startsWith(temp)){//found matching predix
				rs[0]=temp;
				rs[1] = sour.substring(sour.lastIndexOf("/")+1);
				return rs;
			}
			
		}
		
		rs = null;
		return rs;
		
	}
	private List<Statement> GetStatements(List<RDFNode> ob, OntModel m){
		
		List<Statement> rs = new ArrayList<Statement>();
		Resource tempSubject = null;
		List<RDFNode> tempObjs = new ArrayList<RDFNode>();
		Iterator<RDFNode> itObjs = ob.iterator();
		
		while(itObjs.hasNext()){
			tempSubject = itObjs.next().asResource();
			StmtIterator itNew = m.listStatements(tempSubject, null, (RDFNode)null);
			while(itNew.hasNext()){
				Statement curSt = itNew.next();
				rs.add(curSt);
				if(!curSt.getSubject().toString().startsWith("http://")){
					tempObjs.add(curSt.getSubject());
				}
			}
		}
		
		if(tempObjs.size() >0){
			rs.addAll(GetStatements(tempObjs, m));
		}
		
		
		return rs;
	}
	**/
	
	}

