package 검색기;

import java.io.*;
import java.util.Scanner ;
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
import java.util.Arrays ; 

class Cos implements Comparable<Cos>
{
	int docNum ;
	double similarity ; 

	void insert(int a, double b)
	{
		docNum = a ;
		similarity = b ;
	}

	void print()
	{
		System.out.println(", 코사인유사도:" + similarity +  " , 문서번호 :" + docNum) ;
	}

	public int compareTo(Cos cos) {




		if (this.similarity > cos.similarity) {

			return -1;

		} else if (this.similarity== cos.similarity) {

			return 0;

		} else {

			return 1;

		}

	}

}

public class search {

	public static void main(String[] args) {
		try
		{
			EnvironmentConfig envConf = new EnvironmentConfig();
			envConf.setAllowCreate(true);
			Environment dbEnv = new Environment(new File("C:/Users/icd04/Desktop/자바 프로젝트(정보검색)/데이터베이스"), envConf);
			DatabaseConfig dbConf = new DatabaseConfig();
			dbConf.setAllowCreate(true);
			Database IndexWordDB = dbEnv.openDatabase(null, "IndexWordDB", dbConf);
			Database searchDB = dbEnv.openDatabase(null, "searchDB", dbConf);
			Database docDB[] = new Database[31] ;


			for(int i=1 ; i<=30 ; i++)
			{
				docDB[i] =  dbEnv.openDatabase(null, "docDB" + Integer.toString(i), dbConf);
			}

			DatabaseEntry key = new DatabaseEntry();
			// data
			DatabaseEntry data = new DatabaseEntry();

			Cursor myCursor = null;

			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();	

			myCursor = IndexWordDB.openCursor(null, null);
			while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//searchDB를 IndexDB와 똑같이 만드는 과정
			{

				searchDB.put(null, foundKey, foundData);
			}

			myCursor.close(); 

			Scanner scanner = new Scanner(System.in) ;

			while(true)
			{
				String g = new String() ;
				System.out.println("검색을 하세요.(종료하려면 0을 입력하세요)");
				g = scanner.nextLine() ;
				
				if(g.equals("0")) break ;

				String s = new String() ;

				for(int i=0 ; i<g.length() ; i++)//searchDB에 단어가 몇번 나왔는지 저장하는 과정
				{
					if((char)g.charAt(i)!=' '&&(char)g.charAt(i)!='\r'&&(char)g.charAt(i)!='\n')//띄어쓰기하고 엔터 안나오면
					{
						s = s + (char)g.charAt(i) ;

						if(i==g.length()-1)
						{
							if(s.equals(""))continue ;
							if(s.length()==1)//길이가 1이면
							{
								StringBinding.stringToEntry(s, key);


								if(searchDB.get(null,key, data, null)==OperationStatus.SUCCESS)
								{
									int a = IntegerBinding.entryToInt(data) + 1 ;
									IntegerBinding.intToEntry(a, data);
									searchDB.put(null, key, data);

								}




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


										if(searchDB.get(null,key, data, null)==OperationStatus.SUCCESS)
										{
											int a = IntegerBinding.entryToInt(data) + 1 ;
											IntegerBinding.intToEntry(a, data);
											searchDB.put(null, key, data);
										}


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



					else //띄어쓰기하고 엔터가 나오면
					{
						if(s.equals(""))continue ;
						if(s.length()==1)//길이가 1이면
						{
							StringBinding.stringToEntry(s, key);


							if(searchDB.get(null,key, data, null)==OperationStatus.SUCCESS)
							{
								int a = IntegerBinding.entryToInt(data) + 1 ;
								IntegerBinding.intToEntry(a, data);
								searchDB.put(null, key, data);

							}




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


									if(searchDB.get(null,key, data, null)==OperationStatus.SUCCESS)
									{
										int a = IntegerBinding.entryToInt(data) + 1 ;
										IntegerBinding.intToEntry(a, data);
										searchDB.put(null, key, data);
									}


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

				for(int i=1 ; i<=30 ; i++)
				{
					myCursor = searchDB.openCursor(null, null);
					double sum = 0 ;
					while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//searhDB에 그 벡터크기를 저장하는 과정
					{
						if(IntegerBinding.entryToInt(foundData)!=0)
						{
							sum+=Math.pow((double)IntegerBinding.entryToInt(foundData), 2) ;
						}
					}

					myCursor.close();
					StringBinding.stringToEntry("size", key);
					DoubleBinding.doubleToEntry(Math.sqrt(sum) , data);
					searchDB.put(null, key, data) ;
				}



				/*//searchDB에 size가 잘들어 갔나 확인
				if(searchDB.get(null,key, data, null)==OperationStatus.SUCCESS)
				{
					System.out.println("Key  :" + StringBinding.entryToString(key));
					System.out.println("Data :" + DoubleBinding.entryToDouble(data));
				}*/


				Cos[] c = new Cos[30] ;
				for(int i=0 ; i<30 ; i++)
				{
					c[i] = new Cos() ;
				}
				for(int i=0 ; i<30 ; i++)//Cos객체에게 문서 번호와 코사인유사도를 저장하는 과정 
				{
					myCursor = searchDB.openCursor(null, null);
					double sum = 0 ;
					while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
					{
						if(IntegerBinding.entryToInt(foundData)!=0&&StringBinding.entryToString(foundKey).equals("size")==false)
						{
							docDB[i+1].get(null,foundKey, data, null) ;
							double a = IntegerBinding.entryToInt(foundData) ;
							double b = DoubleBinding.entryToDouble(data) ;
							sum += a*b ;
						}
					}
					StringBinding.stringToEntry("size", key);
					docDB[i+1].get(null,key, data, null) ;
					double a = DoubleBinding.entryToDouble(data) ;
					searchDB.get(null,key, data, null) ;
					double b = DoubleBinding.entryToDouble(data) ;
					double simil = sum/(a*b);

					c[i].insert(i+1,simil) ;
					myCursor.close() ;
				}

				Arrays.sort(c);// 정렬


				for(int i=0 ; i<30 ; i++)//cos클래스 출력
				{
					StringBinding.stringToEntry("id", key);
					if(	docDB[c[i].docNum].get(null,key, data, null) ==OperationStatus.SUCCESS)
						System.out.print("ID: "+StringBinding.entryToString(data));


					StringBinding.stringToEntry("title", key);
					if(	docDB[c[i].docNum].get(null,key, data, null)==OperationStatus.SUCCESS)
						System.out.print(", Title: "+StringBinding.entryToString(data));

					c[i].print();

				}

				myCursor = IndexWordDB.openCursor(null, null);
				while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//searchDB를 IndexDB와 똑같이 만드는 과정
				{

					searchDB.put(null, foundKey, foundData);
				}

				myCursor.close(); 
				
			
			}








			/*//searchDB에 단어의 개수가 잘 나왔는지 확인
				myCursor = searchDB.openCursor(null, null);

				while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//IndexDB출력
				{
					System.out.println("Key  :" + StringBinding.entryToString(foundKey));
					System.out.println("Data :" + IntegerBinding.entryToInt(foundData));
				}

				myCursor.close();*/





			for(int i=1 ; i<=30 ; i++)
			{
				docDB[i].close();
			}

			searchDB.close() ;
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

