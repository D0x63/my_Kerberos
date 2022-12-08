package Serverconnect;

import java.util.Arrays;
import java.util.Scanner;

public class Symmetrical_Decode {
	static int [] binarytxt=new int [64];
	static int [] l0=new int [32];
	static int [] l1=new int [32];
	static int [] r0=new int [32];
	static int [] r1=new int [32];
	static int [] EtoP=new int [48];
	static int [][]keys=new int [16][48];
	static String finresult="";
	static int []IP= 
		{58,50,42,34,26,18,10,2,
		 60,52,44,36,28,20,12,4,
		 62,54,46,38,30,22,14,6,
		 64,56,48,40,32,24,16,8,
		 57,49,41,33,25,17,9,1,
		 59,51,43,35,27,19,11,3,
		 61,53,45,37,29,21,13,5,
		 63,55,47,39,31,23,15,7};
	static int[] IP_1= {
			40, 8, 48, 16, 56, 24, 64, 32,
			39, 7, 47, 15, 55, 23, 63, 31,
			38, 6, 46, 14, 54, 22, 62, 30,
			37, 5, 45, 13, 53, 21, 61, 29,
			36, 4, 44, 12, 52, 20, 60, 28,
			35, 3, 43, 11, 51, 19, 59, 27,
			34, 2, 42, 10, 50, 18, 58, 26,
			33, 1, 41, 9, 49, 17, 57, 25};
	static int []EPBox = {
			32, 1, 2, 3, 4, 5,
			4, 5, 6, 7, 8, 9,
			8, 9, 10, 11, 12, 13,
			12, 13, 14, 15, 16, 17,
			16, 17, 18, 19, 20, 21,
			20, 21, 22, 23, 24, 25,
			24, 25, 26, 27, 28, 29,
			28,29,30,31,32,1};
	static int []shift = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
	static int []pc_1= {57,49,41,33,25,17,9,
			1,58,50,42,34,26,18,
			10,2,59,51,43,35,27,
			19,11,3,60,52,44,36,
			63,55,47,39,31,23,15,
			7,62,54,46,38,30,22,
			14,6,61,53,45,37,29,
			21,13,5,28,20,12,4};
	static int []pc_2= {14,17,11,24,1,5,3,28,
			15,6,21,10,23,19,12,4,
			26,8,16,7,27,20,13,2,
			41,52,31,37,47,55,30,40,
			51,45,33,48,44,49,39,56,
			34,53,46,42,50,36,29,32};
	static int [][][]s={
		//S1
		{{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
        {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
        {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
        {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}},
        //S2
         {{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
         {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
         {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
         {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}},
         //S3
         {{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
         {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
         {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
         {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}},
         //S4
         {{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
         {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
         {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
         {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}},
         //S5
         {{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
         {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
         {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
         {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}},
         //S6
         {{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
         {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
         {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
         {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}},
         //S7
         {{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
         {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
         {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
         {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}},
         //S8
         {{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
         {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
         {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
         {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}}};
	static int pBox[] =
		{
		16,7,20,21,29, 12, 28, 17,1, 15, 23, 26,5, 18, 31, 10,
		2,  8, 24, 14, 32, 27, 3,  9, 19,13, 30, 6, 22,11, 4,  25
		};
		

	
	public static String decode(String information,String key)
	{
		//System.out.println("请输入加密密钥");
		//Scanner scanner1 = new Scanner(System.in);
		//String key = scanner1.nextLine();
		getkeys(Mtobin(StrToHex(key)));
		
		//while(true)
		//{
		//	System.out.println("请输入密文");
		//	Scanner scanner2 = new Scanner(System.in);
		//	String text = scanner2.nextLine();	
			splitostring(information);
		//	System.out.println(finresult);
		//	finresult="密文的明文是：";
		//}
			return finresult;
	}
	public static void splitostring(String M)
	{
		int len=M.length();
		long start=System.currentTimeMillis();
		for(int i=0;i<len/16;i++)
		{
			jiemi(M.substring(i*16, i*16+16));
		}
		long end=System.currentTimeMillis();
		System.out.println("此次解密运行时间： "+(end-start)+"ms");
	}
	public static void jiemi(String one)
	{
		int []miwen=Mtobin(one);
		int [] zhihuanmiwen=new int[64];
		zhihuanmiwen=zhengzhihuan(miwen);
		int []swlr=new int [64];
		for(int i=0;i<32;i++)
		{
			swlr[i]=zhihuanmiwen[i+32];
			swlr[i+32]=zhihuanmiwen[i];
		}
		firstsplit(swlr);
		for(int i=0;i<16;i++)
		{
			System.arraycopy(r0, 0, l1, 0, 32);
			EPchange();
			System.arraycopy(yihuo(l0,sandpBoxchange(yihuo(EtoP,keys[15-i]))), 0, r1, 0, 32);
			System.arraycopy(l1, 0, l0, 0, 32);
			System.arraycopy(r1, 0, r0, 0, 32);
		}
		int [] precode=new int [64];
		for(int i=0;i<32;i++)
		{
			precode[i]=l0[i];
			precode[i+32]=r0[i];
		}
		
		
		int [] cod=new int [64];
		for(int i=0;i<32;i++)
		{
			cod[i]=precode[i+32];
			cod[i+32]=precode[i];
		}
		int [] mingwen=new int [64];
		mingwen=nizhihuan(cod);
		finresult=finresult+tostring(mingwen);
	}
public static int [] Mtobin(String M)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < M.length(); i++) {
			int ten = Integer.parseInt(M.charAt(i)+"", 16);
			String binary = Integer.toBinaryString(ten);
			int len = binary.length();
			if (len<4) {
				for (int j = 0; j <4-len ; j++) {
					sb.append(0);
				}
			}
			sb.append(binary);
		}
		//System.out.println(sb.toString());
		for (int i = 0; i < sb.length(); i++) {
			binarytxt[i] = Integer.parseInt(sb.toString().charAt(i)+"");
		}
		//System.out.println("Mtobinary");
		return binarytxt;
	}
public static String StrToHex(String M) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < M.length(); i++) {
			int ten = (int)M.charAt(i);
			String sixteen = Integer.toHexString(ten);
			if (sixteen.length()==1) {
				sb.append(0);
			}
			sb.append(sixteen);
		}
		//System.out.println("to16");
		return sb.toString();
	}
public static int [] zhengzhihuan(int M[]) {
	int [] temp=new int [64];
	for (int i=0;i<64;i++)
	{
		temp[i]=M[IP[i]-1];
	}
	//System.out.println("zhengzhi");
	return temp;
}
public static int [] nizhihuan(int M[]) {
	int [] temp=new int [64];
	for (int i=0;i<64;i++)
	{
		temp[i]=M[IP_1[i]-1];
	}
	//System.out.println("nizhi");
	return temp;
}
public static void firstsplit(int M[]){
	System.arraycopy(M, 0, l0, 0, 32);
	System.arraycopy(M, 32, r0, 0, 32);
	//System.out.println("firstsplit");
	//System.out.println(Arrays.toString(l0));
	//System.out.println(Arrays.toString(r0));
}
public static void EPchange() {
	for(int i=0;i<48;i++)
	{
		EtoP[i]=r0[EPBox[i]-1];
	}
	//System.out.println("epchange");
}
public static void getkeys(int k[]) {//k64
	int [] lk=new int[28];
	int [] rk=new int[28];
	for(int i=0;i<56;i++)
	{
		
		if(i<28)
		{
			lk[i]=k[pc_1[i]-1];
		}
		else if(i>=28)
		{
			rk[i-28]=k[pc_1[i]-1];
		}
	}
	
	for(int j=0;j<16;j++)
	{
		if(shift[j]==1)
		{
			int templ,tempr;
			templ=lk[0];
			tempr=rk[0];
			for(int m=1;m<28;m++)
			{
				lk[m-1]=lk[m];
				rk[m-1]=rk[m];
			}
			lk[27]=templ;
			rk[27]=tempr;
		}
		if(shift[j]==2)
		{
			int templ0,tempr0,templ1,tempr1;
			templ0=lk[0];
			tempr0=rk[0];
			templ1=lk[1];
			tempr1=rk[1];
			int m=2;
			while(m<28)
			{
				lk[m-2]=lk[m];
				rk[m-2]=rk[m];
				lk[m-1]=lk[m+1];
				rk[m-1]=rk[m+1];
				m=m+2;
			}
			lk[26]=templ0;
			rk[26]=tempr0;
			lk[27]=templ1;
			rk[27]=tempr1;
		}
		for(int n=0;n<48;n++)
		{
			if(n<24)
			{
				keys[j][n]=lk[pc_2[n]-1];
			}
			else
			{
				keys[j][n]=rk[pc_2[n]-29];
			}
		}
	}
	//System.out.println("getkeys");
}
public static int [] sandpBoxchange(int M[])
{
	int i=0;
	int [] ran=new int[8];
	int [] col=new int[8];
	int [] resultten=new int[8];
	int [] resulttwo=new int[32];
	int [] result=new int[32];
	while(i<48)
	{
		ran[i/6]=M[i]*2+M[i+5];
		col[i/6]=M[i+1]*(2^3)+M[i+2]*(2^2)+M[i+3]*2+M[i+4];
		i=i+6;
	}
	for(int j=0;j<8;j++)
	{
		resultten[j]=s[j][ran[j]][col[j]];
	}
	for(int j=0;j<8;j++)
	{
		int n=0;
		n=resultten[j];
		for(int k=0;k<4;k++)
		{
			resulttwo[(j+1)*4-k-1]=n%2;
			n=n/2;
		}
	}
	for(int j=0;j<32;j++)
	{
		result[j]=resulttwo[pBox[j]-1];
	}
	//System.out.println("spcahnge");
	return result;
}
public static int [] yihuo(int[] a,int[] b) {
	int len=a.length;
	int [] result=new int[len];
	for(int i=0;i<len;i++)
	{
		result[i]=a[i]^b[i];
	}
	//System.out.println("yihuo");
	return result;
}
public static String tostring(int code[]) {

	StringBuilder sb = new StringBuilder();
	String c = "";
	for (int i = 0; i < code.length; i++) {
		c = c + code[i];
	}
	for (int i = 0; i*8+8 <= c.length(); i++) {
		String str = c.substring(i*8, i*8+8);
		int ten = Integer.parseInt(str,2);
		char ch = (char)ten;
		sb.append(ch);
	}
	return sb.toString();
}


}
