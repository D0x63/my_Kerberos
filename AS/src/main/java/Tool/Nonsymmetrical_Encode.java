package Tool;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class Nonsymmetrical_Encode {
    public static BigInteger p1,q1,n1,fn1,e1,d1;
    public static int weishu=5; //p,q的位数
    public Nonsymmetrical_Encode() {
        getpandq(); //得到p和q
        gete(); //得到e
        BigInteger[] temp =getd(e1,fn1);
        d1=temp[0];
    }
    public BigInteger returnE()
    {
        return e1;
    }
    public BigInteger returnN()
    {
        return n1;
    }
    public BigInteger returnD()
    {
        return d1;
    }


    public static String jiami(String M,String E2,String N2,String D1,String N1)//M是要加密信息，E2，N2是对方的公钥,D1,N1是自身的私钥
    {
        BigInteger e2=new BigInteger(E2);
        BigInteger n2=new BigInteger(N2);
        BigInteger d1=new BigInteger(D1);
        BigInteger n1=new BigInteger(N1);
        BigInteger c;
        String code="密文是：";
        String hashcode = null,codesign = "*";
        //得到hash值
        try {
            hashcode=hash(M);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //得到数字签名
        int lengthofhash=hashcode.length();
        for(int i=0;i<lengthofhash;i++)
        {
            int order1=(int)hashcode.charAt(i);
            BigInteger sig= BigInteger.valueOf(order1);
            c=sig.modPow(d1, n1);
            codesign=codesign+c+'-';	//最终codesign长度为1+32
        }
        //System.out.println(codesign);//数字签名密文

        //得到信息密文
        int length=M.length();
        for(int i=0;i<length;i++)
        {
            int order=(int)M.charAt(i);
            BigInteger m= BigInteger.valueOf(order);
            c=m.modPow(e2, n2);
            code=code+c+'-';
        }
        code=code+codesign;//信息密文和数字签名使用*间隔，
        return code.substring(4);//返回信息是密文和数字签名的结合，两者以*间隔
    }
    public static String jiemi(String C,String E2,String N2,String D1,String N1)//C应该是要解密的密文和数字签名的结合体，以*间隔，返回结果是数组，信息明文+判断是否发送方正确
    {//D1，N1是自身的私钥，E2，N2是对方的公钥

        BigInteger e2=new BigInteger(E2);
        BigInteger n2=new BigInteger(N2);
        BigInteger d1=new BigInteger(D1);
        BigInteger n1=new BigInteger(N1);

        String result="";

        //处理密文信息部分
        BigInteger m1;
        char cc;
        String decode="明文是：",str=" ";
        String information=C.substring(0, C.indexOf('*'));
        int length1=information.length();
        for(int i=0;i<length1;i++)
        {
            if(C.charAt(i)!='-')
            {
                str=str+C.charAt(i);
            }
            else if(C.charAt(i)=='-')
            {
                BigInteger c= new BigInteger(str.substring(1));
                m1=c.modPow(d1, n1);
                cc=(char)m1.intValue();
                decode=decode+cc;
                str=" ";
            }
        }

        //System.out.println(decode.substring(4));//s输出明文信息

        //处理数字签名部分
        String M,hashcode = null,signcode,sign;
        signcode=C.substring(C.indexOf('*')+1);//数字签名密文
        try {
            hashcode=hash(decode.substring(4));//接收者计算数字签名
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int lenofs=signcode.length();//接收到的数字签名密文的长度
        BigInteger bigofs;
        char wofs;
        String signm = " ",str1=" ";
        for(int i=0;i<lenofs;i++)
        {
            if(signcode.charAt(i)!='-')
            {
                str1=str1+signcode.charAt(i);//未到字母分割符号‘-’时，拼接一个字母的密文
            }
            else if(signcode.charAt(i)=='-')
            {
                BigInteger c1=new BigInteger(str1.substring(1));//第一位是空格
                bigofs=c1.modPow(e2, n2);//解密一个字母的十进制
                wofs=(char)bigofs.intValue();//解密一个字母的字符
                signm=signm+wofs;//拼接每个字母为数字签名明文
                str1=" ";//一个字母初始状态
            }
        }
        result= decode.substring(4);//返回结果数组中，第一个为信息明文
        if((signm.substring(1)).equals(hashcode))
        {
            result ="true*"+result;
        }
        else
        {
            result+="flase*"+result;
        }
        return result;
    }
    static BigInteger getRandom(int length) {

        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        if(val.charAt(0)=='0')
        {
            val=val.substring(1);
            val=val+String.valueOf(random.nextInt(10));//得到指定长的随机数  字符串型
        }
        BigInteger ran = new BigInteger(val);
        return ran;

    }
    public static void getpandq() { //得到p,q
        int m=0,k = 0;
        while(true)
        {
            p1=getRandom(weishu);//得到指定位数的随机数
            if(p1.isProbablePrime(100))
            {
                System.out.println("质数p是："+p1);
                break;
            }
            else
            {
                continue;
            }
        }
        while(true)
        {
            q1=getRandom(weishu);
            if(q1.isProbablePrime(100))
            {
                System.out.println("质数q是："+q1);
                break;
            }
            else
            {
                continue;
            }
        }
        n1=p1.multiply(q1);//公钥
        fn1=(p1.subtract(new BigInteger("1"))).multiply(q1.subtract(new BigInteger("1")));//计算与p*q的乘积的数互质的数的个数
    }
    static boolean isrp(BigInteger a, BigInteger b){//判断两数是否互质
        if(a==new BigInteger("1")||b==new BigInteger("1"))
            return true;
        BigInteger t = null;
        while(true){

            t = a.mod(b);
            //if(t == new BigInteger("0")) break;
            if(t.compareTo(new BigInteger("0"))==0)break;
            else{
                a = b;
                b = t;
            }
        }
        if(b.compareTo(new BigInteger("1"))>0)	return false;// 如果最大公约数大于1，表示两个正整数不互质
        else return true;	// 如果最大公约数等于1,表示两个正整数互质
    }
    static void gete()//e的值和fn是互质的且足够大，公钥
    {
        BigInteger i=new BigInteger("65537");
        while(true)
        {
            if(isrp(i,fn1))
            {
                e1=i;
                break;
            }
            else
            {
                i.add(new BigInteger("1"));
                continue;
            }
        }
    }
    static BigInteger[] getd(BigInteger a,BigInteger b)//辗转相除得到d，d*e和fn互质
    {
        BigInteger[] result = new BigInteger[2];
        if(b.compareTo(BigInteger.valueOf(0)) == 0){
            result[0] = BigInteger.valueOf(1);
            result[1] = BigInteger.valueOf(0);
            return result;
        }
        BigInteger[] temp = getd(b, a.mod(b));
        result[0] = temp[1];
        result[1] = temp[0].subtract((a.divide(b)).multiply(temp[1]));
        return result;
    }
    static String hash(String str) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 反复调用update输入数据:
        //md.update("Hello".getBytes("UTF-8"));
        md.update(str.getBytes("UTF-8"));
        byte[] result = md.digest();
        return new BigInteger(1, result).toString(16);
    }

    public static void  main(String args[])
    {

        Nonsymmetrical_Encode RSA1=new Nonsymmetrical_Encode();
        Nonsymmetrical_Encode RSA2=new Nonsymmetrical_Encode();
        BigInteger n2=RSA2.returnN(); //得到R2的公钥
        BigInteger e2=RSA2.returnE();
        BigInteger d2=RSA2.returnD();//得到R2的私钥n2
        System.out.println(n2.toString());

        BigInteger n1=RSA1.returnN();//得到R1的公钥
        BigInteger e1=RSA1.returnE();
        BigInteger d1=RSA1.returnD();//得到R1的私钥
        String plaintext="";
        String ciphertext=Nonsymmetrical_Encode.jiami(plaintext,e2.toString(),n2.toString(),d1.toString(),n1.toString());
        System.out.println(ciphertext);
        String plaintexts= Nonsymmetrical_Encode.jiemi(ciphertext,e1.toString(),n1.toString(),d2.toString(),n2.toString());
        if(plaintexts.split("\\*",2)[0].equals("true"))
        {
            System.out.println(plaintexts.split("\\*",2)[1]);
        }
        else {
            System.out.println("签名错误");
        }
    }

}
