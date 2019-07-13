package ���α�;

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
					new File("C:/Users/icd04/Desktop/�ڹ� ������Ʈ(�����˻�)/�����ͺ��̽�"), envConf);
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
				in[i] = new FileReader("C:/Users/icd04/Desktop/�ڹ� ������Ʈ(�����˻�)/konews/"+Integer.toString(i+1)+".txt");
			}

			int c ;
			String s = new String() ;

			for(int i=0 ; i<30 ; i++)//IndexWordDB�� ����� ����
			{
				while((c=in[i].read())!=-1)
				{

					if((char)c!=' '&&(char)c!='\r'&&(char)c!='\n')//�����ϰ� ���� �ȳ�����
					{
						s = s + (char)c ;
					}

					else //�����ϰ� ���Ͱ� ������
					{
						if(s.equals(""))continue ;
						if(s.length()==1)//���̰� 1�̸�
						{
							StringBinding.stringToEntry(s, key);
							//IntegerBinding.intToEntry(1, data);
							IntegerBinding.intToEntry(0, data);
							if(IndexWordDB.get(null,key, data, null)!=OperationStatus.SUCCESS)//ó�� ������ �Ÿ�
							{
								IndexWordDB.put(null, key, data);

							}

							//else//������ �ִ� �Ÿ�
							//{
							//int a = IntegerBinding.entryToInt(data) + 1 ;
							//IntegerBinding.intToEntry(a, data);
							//IndexWordDB.put(null, key, data);

							//}

							s = "" ;//��Ʈ�� �ʱ�ȭ

						}

						else//���̰� 1�� �ƴϸ�
						{
							int k = 0 ;
							int u = 0 ;
							String st = new String();
							while(true)
							{
								st+=s.charAt(k++);
								u++ ;
								if(u==2)//���ڰ� �ΰ��� �Ǹ�
								{
									StringBinding.stringToEntry(st, key);
									//IntegerBinding.intToEntry(1, data);
									IntegerBinding.intToEntry(0, data);
									if(IndexWordDB.get(null,key, data, null)!=OperationStatus.SUCCESS)//ó�� ������ �Ÿ�
									{
										IndexWordDB.put(null, key, data);

									}

									//else//������ �ִ� �Ÿ�
									//{
									//int a = IntegerBinding.entryToInt(data) + 1 ;
									//IntegerBinding.intToEntry(a, data);
									//IndexWordDB.put(null, key, data);

									//}
									k-- ;
									u=0 ;
									st="";

									if(k==s.length()-1)//���ڿ��� ��������
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

			while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//docDB�� IndexDB�� �Ȱ��� ����� ����
			{
				for(int i=1 ; i<=30 ; i++)
				{
					docDB[i].put(null, foundKey, foundData);
				}
			}

			myCursor.close();

			/*//IndexWorDB �� ���� ���
			myCursor = IndexWordDB.openCursor(null, null);


			while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//IndexDB���
			{
				System.out.println("Key  :" + StringBinding.entryToString(foundKey));
				System.out.println("Data :" + IntegerBinding.entryToInt(foundData));
			}
			myCursor.close();
			 */

			for(int i=0 ; i<30 ; i++)//���ϸ����� �� �ݰ�
			{
				in[i].close();
			}
			for(int i=0; i<30 ; i++)//�ٽ� �ؽ�Ʈ�� �о� ����
			{
				in[i] = new FileReader("C:/Users/icd04/Desktop/�ڹ� ������Ʈ(�����˻�)/konews/"+Integer.toString(i+1)+".txt");
			}

		
			for(int i=1; i<=30 ; i++)//docDB�鿡�� �� �ܾ ����� �������� �����ϴ� ����
			{
				while((c=in[i-1].read())!=-1)
				{
					if((char)c!=' '&&(char)c!='\r'&&(char)c!='\n')//�����ϰ� ���� �ȳ�����
					{
						s = s + (char)c ;
					}

					else //�����ϰ� ���Ͱ� ������
					{
						if(s.equals(""))continue ;
						if(s.length()==1)//���̰� 1�̸�
						{
							StringBinding.stringToEntry(s, key);
							//IntegerBinding.intToEntry(1, data);
							//IntegerBinding.intToEntry(0, data);
							//if(IndexWordDB.get(null,key, data, null)!=OperationStatus.SUCCESS)//ó�� ������ �Ÿ�
							//{
							//	IndexWordDB.put(null, key, data);

							//}

							//������ �ִ� �Ÿ�

							docDB[i].get(null,key, data, null) ;
							int a = IntegerBinding.entryToInt(data) + 1 ;
							IntegerBinding.intToEntry(a, data);
							docDB[i].put(null, key, data);



							s = "" ;//��Ʈ�� �ʱ�ȭ

						}

						else//���̰� 1�� �ƴϸ�
						{
							int k = 0 ;
							int u = 0 ;
							String st = new String();
							while(true)
							{
								st+=s.charAt(k++);
								u++ ;
								if(u==2)//���ڰ� �ΰ��� �Ǹ�
								{
									StringBinding.stringToEntry(st, key);
									//IntegerBinding.intToEntry(1, data);
									//IntegerBinding.intToEntry(0, data);
									//if(IndexWordDB.get(null,key, data, null)!=OperationStatus.SUCCESS)//ó�� ������ �Ÿ�
									//{
									//	IndexWordDB.put(null, key, data);

									//}

									//������ �ִ� �Ÿ�

									docDB[i].get(null,key, data, null) ;
									int a = IntegerBinding.entryToInt(data) + 1 ;
									IntegerBinding.intToEntry(a, data);
									docDB[i].put(null, key, data);

									k-- ;
									u=0 ;
									st="";

									if(k==s.length()-1)//���ڿ��� ��������
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


			Database wordDfDB = dbEnv.openDatabase(null, "wordDfDB", dbConf);//�� �ܾ��� Df�� �����ϰ� �ִ� DB ����
			Cursor myCursor2 = null;
			myCursor2 = IndexWordDB.openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				wordDfDB.put(null, foundKey, foundData);
			}
			myCursor2.close() ;




			myCursor2 = wordDfDB.openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)//Df��� ����� ����
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


			/*wordDfDB �ߵ�� ���� ���
			myCursor2 = wordDfDB.openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				System.out.println("Key  :" + StringBinding.entryToString(foundKey));
				System.out.println("Data :" + IntegerBinding.entryToInt(foundData));
			}

			myCursor2.close() ;*/



			for(int i=1 ; i<=30 ; i++)//docDB�鿡�� tf-idf ����ġ �Է�
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
			//tf-idf �ߵ��� Ȯ��
			myCursor2 = docDB[31].openCursor(null, null);
			while (myCursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				System.out.println("Key  :" + StringBinding.entryToString(foundKey));
				System.out.println("Data :" + DoubleBinding.entryToDouble(foundData));
			}
			myCursor2.close() ;*/


			for(int i=0 ; i<30 ; i++)//���ϸ����� �� �ݰ�
			{
				in[i].close();
			}
			for(int i=0; i<30 ; i++)//�ٽ� �ؽ�Ʈ�� �о� ����
			{
				in[i] = new FileReader("C:/Users/icd04/Desktop/�ڹ� ������Ʈ(�����˻�)/konews/"+Integer.toString(i+1)+".txt");
			}

			

			for(int i=0 ; i<30 ; i++)//docDB�鿡�� id�� title�� �����ϴ� ����
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

			/* docDB�� id�� title�� �ߵ��� Ȯ��
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

			for(int i=1 ; i<=30 ; i++)//docDB�鿡�� ������ ũ�� size�� �����ϴ� ����
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
			
			
			/*//docDB�� size�� �ߵ����� Ȯ��
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
			
			System.out.println("������ �������ϴ�.");
			

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
