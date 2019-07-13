package 색인기;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import com.sleepycat.bind.tuple.DoubleBinding;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryDatabase;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;

public class J {

	public static void main(String[] args) {
		try
		{
			EnvironmentConfig envConf = new EnvironmentConfig();
			envConf.setAllowCreate(true);
			Environment dbEnv = new Environment(
					new File("C:/Users/icd04/Desktop/자바 프로젝트(정보검색)/데이터베이스"), envConf);
			DatabaseConfig dbConf = new DatabaseConfig();
			dbConf.setAllowCreate(true);
			Database IndexWordDB = dbEnv.openDatabase(null, "IndexWordDB", dbConf);
			Database docDB[] = new Database[31] ;
			for(int i=1 ; i<=30 ; i++)
			{
				docDB[i] =  dbEnv.openDatabase(null, "docDB" + Integer.toString(i), dbConf);
			}

			DatabaseEntry key = new DatabaseEntry();
			// data
			DatabaseEntry data = new DatabaseEntry();



			FileReader[] in = new FileReader[40] ;
			for(int i=0; i<30 ; i++)
			{
				in[i] = new FileReader("C:/Users/icd04/Desktop/자바 프로젝트(정보검색)/konews/"+Integer.toString(i+1)+".txt");
			}

			int c ;
			String s = new String() ;

			for(int i=0 ; i<30 ; i++)//IndexWordDB를 만드는 과정
			{
				while((c=in[i].read())!=-1)
				{

					if((char)c!=' '&&(char)c!='\r'&&(char)c!='\n')//띄어쓰기하고 엔터 안나오면
					{
						s = s + (char)c ;
					}

					else //띄어쓰기하고 엔터가 나오면
					{
						if(s.equals(""))continue ;
						if(s.length()==1)//길이가 1이면
						{
							StringBinding.stringToEntry(s, key);
							//IntegerBinding.intToEntry(1, data);
							IntegerBinding.intToEntry(0, data);
							if(IndexWordDB.get(null,key, data, null)!=OperationStatus.SUCCESS)//처음 나오는 거면
							{
								IndexWordDB.put(null, key, data);

							}

							//else//기존에 있는 거면
							//{
							//int a = IntegerBinding.entryToInt(data) + 1 ;
							//IntegerBinding.intToEntry(a, data);
							//IndexWordDB.put(null, key, data);

							//}

							s = "" ;//스트링 초기화

						}

						else//길이가 1이 아니면
						{
							int k = 0 ;
							int u = 0 ;
							String st = new String();
							while(true)
							{
								st+=s.charAt(k++);
								u++ ;
								if(u==2)//글자가 두개가 되면
								{
									StringBinding.stringToEntry(st, key);
									//IntegerBinding.intToEntry(1, data);
									IntegerBinding.intToEntry(0, data);
									if(IndexWordDB.get(null,key, data, null)!=OperationStatus.SUCCESS)//처음 나오는 거면
									{
										IndexWordDB.put(null, key, data);

									}

									//else//기존에 있는 거면
									//{
									//int a = IntegerBinding.entryToInt(data) + 1 ;
									//IntegerBinding.intToEntry(a, data);
									//IndexWordDB.put(null, key, data);

									//}
									k-- ;
									u=0 ;
									st="";

									if(k==s.length()-1)//문자열이 끝났을때
									{
										s="";
										break ;
									}
								}


							}
						}

					}

				}
			}


			Cursor myCursor = null;
			myCursor = IndexWordDB.openCursor(null, null);


			// Cursors returns records as pairs of DatabaseEntry objects
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();	

			// Retrieve records with calls to getNext() until the
			// return status is not OperationStatus.SUCCESS

			while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//docDB를 IndexDB와 똑같이 만드는 과정
			{
				for(int i=1 ; i<=30 ; i++)
				{
					docDB[i].put(null, foundKey, foundData);
				}
			}

			myCursor.close();

			/*//IndexWorDB 잘 들어갔나 출력
			myCursor = IndexWordDB.openCursor(null, null);


			while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//IndexDB출력
			{
				System.out.println("Key  :" + StringBinding.entryToString(foundKey));
				System.out.println("Data :" + IntegerBinding.entryToInt(foundData));
			}
			myCursor.close();
			 */

			for(int i=0 ; i<30 ; i++)//파일리더를 다 닫고
			{
				in[i].close();
			}
			for(int i=0; i<30 ; i++)//다시 텍스트를 읽어 들임
			{
				in[i] = new FileReader("C:/Users/icd04/Desktop/자바 프로젝트(정보검색)/konews/"+Integer.toString(i+1)+".txt");
			}

		
			for(int i=1; i<=30 ; i++)//docDB들에게 각 단어가 몇번씩 나오는지 저장하는 과정
			{
				while((c=in[i-1].read())!=-1)
				{
					if((char)c!=' '&&(char)c!='\r'&&(char)c!='\n')//띄어쓰기하고 엔터 안나오면
					{
						s = s + (char)c ;
					}

					else //띄어쓰기하고 엔터가 나오면
					{
						if(s.equals(""))continue ;
						if(s.length()==1)//길이가 1이면
						{
							StringBinding.stringToEntry(s, key);
							//IntegerBinding.intToEntry(1, data);
							//IntegerBinding.intToEntry(0, data);
							//if(IndexWordDB.get(null,key, data, null)!=OperationStatus.SUCCESS)//처음 나오는 거면
							//{
							//	IndexWordDB.put(null, key, data);

							//}

							//기존에 있는 거면

							docDB[i].get(null,key, data, null) ;
							int a = IntegerBinding.entryToInt(data) + 1 ;
							IntegerBinding.intToEntry(a, data);
							docDB[i].put(null, key, data);



							s = "" ;//스트링 초기화

						}

						else//길이가 1이 아니면
						{
							int k = 0 ;
							int u = 0 ;
							String st = new String();
							while(true)
							{
								st+=s.charAt(k++);
								u++ ;
								if(u==2)//글자가 두개가 되면
								{
									StringBinding.stringToEntry(st, key);
									//IntegerBinding.intToEntry(1, data);
									//IntegerBinding.intToEntry(0, data);
									//if(IndexWordDB.get(null,key, data, null)!=OperationStatus.SUCCESS)//처음 나오는 거면
									//{
									//	IndexWordDB.put(null, key, data);

									//}

									//기존에 있는 거면

									docDB[i].get(null,key, data, null) ;
									int a = IntegerBinding.entryToInt(data) + 1 ;
									IntegerBinding.intToEntry(a, data);
									docDB[i].put(null, key, data);

									k-- ;
									u=0 ;
									st="";

									if(k==s.length()-1)//문자열이 끝났을때
									{
										s="";
										break ;
									}
								}
							}
						}
					}

				}
			}


			Database wordDfDB = dbEnv.openDatabase(null, "wordDfDB", dbConf);//각 단어의 Df를 저장하고 있는 DB 생성
			Cursor myCursor2 = null;
			myCursor2 = IndexWordDB.openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				wordDfDB.put(null, foundKey, foundData);
			}
			myCursor2.close() ;




			myCursor2 = wordDfDB.openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//Df디비 만든는 과정
			{
				for(int i=1; i<=30 ; i++)
				{
					String k = StringBinding.entryToString(foundKey) ;
					docDB[i].get(null,foundKey, data, null) ;
					if(IntegerBinding.entryToInt(data)>0)
					{
						wordDfDB.get(null, foundKey, data,null) ;
						int a = IntegerBinding.entryToInt(data) + 1 ;
						IntegerBinding.intToEntry(a, data);
						wordDfDB.put(null, foundKey, data) ;
					}
				}
			}

			myCursor2.close() ;


			/*wordDfDB 잘들어 갔나 출력
			myCursor2 = wordDfDB.openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				System.out.println("Key  :" + StringBinding.entryToString(foundKey));
				System.out.println("Data :" + IntegerBinding.entryToInt(foundData));
			}

			myCursor2.close() ;*/



			for(int i=1 ; i<=30 ; i++)//docDB들에게 tf-idf 가중치 입력
			{
				myCursor2 = docDB[i].openCursor(null, null);
				while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
				{
					double a = Math.log10((double)(1+IntegerBinding.entryToInt(foundData))) ;
					wordDfDB.get(null, foundKey, data,null) ;
					double b = Math.log10(40/(double)IntegerBinding.entryToInt(data)) ;
					DoubleBinding.doubleToEntry(a*b, data);
					docDB[i].put(null, foundKey, data) ;
				}
				docDB[i].put(null, foundKey, data) ;
				myCursor2.close() ;
			}



			/*
			//tf-idf 잘들어갔나 확인
			myCursor2 = docDB[31].openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				System.out.println("Key  :" + StringBinding.entryToString(foundKey));
				System.out.println("Data :" + DoubleBinding.entryToDouble(foundData));
			}
			myCursor2.close() ;*/


			for(int i=0 ; i<30 ; i++)//파일리더를 다 닫고
			{
				in[i].close();
			}
			for(int i=0; i<30 ; i++)//다시 텍스트를 읽어 들임
			{
				in[i] = new FileReader("C:/Users/icd04/Desktop/자바 프로젝트(정보검색)/konews/"+Integer.toString(i+1)+".txt");
			}

			

			for(int i=0 ; i<30 ; i++)//docDB들에게 id와 title을 저장하는 과정
			{
				int r = 0 ;
				int k = 0 ;
				String u = new String();
				while((c=in[i].read())!=-1)
				{
					if((char)c=='>')
					{
						r++ ;
						if(r==2||r==4)
						{
							k=1 ;	
							continue ;
						}
					}

					else if(k==1)
					{

						if((char)c=='<'&&r==2)
						{
							k=0 ;
							StringBinding.stringToEntry("id", key);
							StringBinding.stringToEntry(u, data);
							docDB[i+1].put(null, key, data);
							u="";
						}

						else if((char)c=='<'&&r==4)
						{
							k=0 ;
							StringBinding.stringToEntry("title", key);
							StringBinding.stringToEntry(u, data);
							docDB[i+1].put(null, key, data);
							u="";
							r=0;
							k=0;
							break ;
						}

						if(k!=0) 
							u = u + (char)c ;
					}
				}
			}

			/* docDB에 id와 title이 잘들어갔나 확인
			StringBinding.stringToEntry("id", key);
			if(docDB[1].get(null,key, data, null)==OperationStatus.SUCCESS)
			{
			System.out.println("Key  :" + StringBinding.entryToString(key));
			System.out.println("Data :" + StringBinding.entryToString(data));
			}

			StringBinding.stringToEntry("title", key);
			if(docDB[1].get(null,key, data, null)==OperationStatus.SUCCESS)
			{
				System.out.println("Key  :" + StringBinding.entryToString(key));
				System.out.println("Data :" + StringBinding.entryToString(data));
			}

			StringBinding.stringToEntry("id", key);
			if(docDB[2].get(null,key, data, null)==OperationStatus.SUCCESS)
			{
			System.out.println("Key  :" + StringBinding.entryToString(key));
			System.out.println("Data :" + StringBinding.entryToString(data));
			}

			StringBinding.stringToEntry("title", key);
			if(docDB[2].get(null,key, data, null)==OperationStatus.SUCCESS)
			{
				System.out.println("Key  :" + StringBinding.entryToString(key));
				System.out.println("Data :" + StringBinding.entryToString(data));
			}*/

			for(int i=1 ; i<=30 ; i++)//docDB들에게 벡터의 크기 size를 저장하는 과정
			{
				myCursor = docDB[i].openCursor(null, null);
				double sum = 0 ;
				while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
				{
					if(StringBinding.entryToString(foundKey).equals("title")||StringBinding.entryToString(foundKey).equals("id"))continue;
					if(DoubleBinding.entryToDouble(foundData)!=0)
					{
						sum+=Math.pow((double)DoubleBinding.entryToDouble(foundData), 2) ;
					}
				}

				myCursor.close();
				StringBinding.stringToEntry("size", key);
				DoubleBinding.doubleToEntry(Math.sqrt(sum) , data);
				docDB[i].put(null, key, data) ;
			}
			
			
			/*//docDB에 size가 잘들어갔는지 확인
			myCursor2 = docDB[1].openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				System.out.println("Key  :" + StringBinding.entryToString(foundKey));
				System.out.println("Data :" + DoubleBinding.entryToDouble(foundData));
			}
			myCursor2.close() ;
			if(docDB[1].get(null,key, data, null)==OperationStatus.SUCCESS)
			{
				System.out.println("Key  :" + StringBinding.entryToString(key));
				System.out.println("Data :" + DoubleBinding.entryToDouble(data));
			}*/
			
			System.out.println("색인이 끝났습니다.");
			

			for(int i=0 ; i<30 ; i++)
			{
				in[i].close();
			}

			for(int i=1 ; i<=30 ; i++)
			{
				docDB[i].close();
			}

			wordDfDB.close();
			myCursor.close();
			IndexWordDB.close();
			dbEnv.close();
		}
		catch (DatabaseException dbe) {
			System.out.println("Error :" + dbe.getMessage() );
		}
		catch(Exception e){
			System.out.println(e);
		}

	}

}
