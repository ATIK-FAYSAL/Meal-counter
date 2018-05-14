package com.atik_faysal.others;

import android.widget.Toast;

import com.atik_faysal.mealcounter.AlertDialogClass;
import com.atik_faysal.mealcounter.R;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class DesEncryptionAlgo
{

     private String[] hexaValue = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
     private String cipherText;
     private AlertDialogClass dialogClass;
     private Context context;


     //s-box 4/16 matrix
     private String[][] sBox = new String[][]{
          {"63","7C","77","7B","F2","6B","6F","C5" ,"30","01","67","2B","FE","D7","AB","76"},
          {"CA","82","C9","7D","FA","59","47","F0" ,"AD","D4","A2","AF","9C","A4","72","C0"},
          {"B7","FD","93","26","36","3F","F7","CC" ,"34","A5","E5","F1","71","D8","31","15"},
          {"04","C7","23","C3","18","96","05","9A" ,"07","12","80","E2","EB","27","B2","75"}};


        public DesEncryptionAlgo(Context context)
        {
             this.context = context;
             dialogClass = new AlertDialogClass(context);
        }


     private String customizePlainPass(String pass)
     {
          StringBuilder capitalPass = new StringBuilder(pass.toUpperCase());
          StringBuilder newCapPass = new StringBuilder();
          int passLen = capitalPass.length();
          if(passLen<16)
          {
               for(int i=hexaValue.length-1;i>=0;i--)
               {
                    if(capitalPass.length()==16)break;
                    capitalPass.append(hexaValue[i]);
               }
          }

          for(int i=0;i<capitalPass.length();i++)
          {
               if(capitalPass.charAt(i)=='G'||capitalPass.charAt(i)=='M'||capitalPass.charAt(i)=='S'||capitalPass.charAt(i)=='Y')
                    newCapPass.append("A");
               else if(capitalPass.charAt(i)=='H'||capitalPass.charAt(i)=='N'||capitalPass.charAt(i)=='T'||capitalPass.charAt(i)=='Z')
                    newCapPass.append("B");
               else if(capitalPass.charAt(i)=='I'||capitalPass.charAt(i)=='O'||capitalPass.charAt(i)=='U')
                    newCapPass.append("C");
               else if(capitalPass.charAt(i)=='J'||capitalPass.charAt(i)=='P'||capitalPass.charAt(i)=='V')
                    newCapPass.append("D");
               else if(capitalPass.charAt(i)=='K'||capitalPass.charAt(i)=='Q'||capitalPass.charAt(i)=='W')
                    newCapPass.append("E");
               else if(capitalPass.charAt(i)=='L'||capitalPass.charAt(i)=='R'||capitalPass.charAt(i)=='X')
                    newCapPass.append("F");
               else if ((capitalPass.charAt(i)>='A'&&capitalPass.charAt(i)<='F')||(capitalPass.charAt(i)>='0'&&capitalPass.charAt(i)<='9'))
                    newCapPass.append(capitalPass.charAt(i));
               else newCapPass.append("0");
          }

          return newCapPass.toString();
     }

     public String encryptPass(String plainPass)
     {
          String plainPassword = customizePlainPass(plainPass);
          String permutedText,permutedKey;
          String key = context.getResources().getString(R.string.encryptionKey);
          permutedText = initialPermutation(plainPassword);
          permutedKey = initialPermutation(key);

          int i=1;
          String leftText,rightText,leftKey,rightKey;
          String plainText[] = dividedTextAndKey(permutedText);
          leftText = plainText[0];
          rightText = plainText[1];

          String keyValue[] = dividedTextAndKey(permutedKey.substring(1,15));
          leftKey = keyValue[0];
          rightKey = keyValue[1];
          while(i<=16)
          {
               List<String>values = plainTextToCipherText(leftText,rightText,leftKey,rightKey);
               leftText = values.get(0);
               rightText = values.get(1);

               if(i==16)
                    cipherText = leftText+rightText;

               i++;
          }
          return cipherText;
     }

     //first permutation
     private String initialPermutation(String text)
     {
          StringBuilder permutedText= new StringBuilder();

          int length = text.length()-1;//cause array index is (0-15)

          try {
               for(int i=length;i>=0;i--)
               {
                    if(i%2==0)//first concat with even index character
                         permutedText.append(text.charAt(i));
               }
               for(int i=length;i>=0;i--)
               {
                    if(i%2!=0)//then concat with odd index character
                         permutedText.append(text.charAt(i));
               }
          }catch (StringIndexOutOfBoundsException e)
          {
               dialogClass.error(e.toString());//if any error then show here
          }

          return permutedText.toString();//return permuted string
     }

     //converting here plain text to cipher text
     private List<String> plainTextToCipherText(String leftText,String rightText,String leftKey,String rightKey)
     {
          List<String> values = new ArrayList<>();
          String expPermutedString = initialPermutation(rightText+leftText.substring(3,7));//expand 32 to 48 bit
          String expPermutedKey = leftShiftAndPermutation(leftKey,rightKey);//expand 56 to 48 and left shift

          List<String>binary = xorString(expPermutedString,expPermutedKey,6);//get xor value 48 bit string

          StringBuilder builder = new StringBuilder();
          for(int i=0;i<binary.size();i++)
               builder.append(sBoxValue(binary.get(i)));//get value from sbox

          leftText = rightText;//swap right to left
          rightText = convertTo32Bit(builder.toString());//xorLeftAndRight(convertTo32Bit(builder.toString()),leftText);
          rightText = xorLeftAndRight(leftText,rightText);//last xor value

          values.add(leftText);
          values.add(rightText);
          values.add(expPermutedKey);

          return values;
     }

     //split text and key
     private String[] dividedTextAndKey(String value)
     {
          String text[] = {"",""};
          for(int i=0;i<value.length();i++)
          {
               if(i<value.length()/2)text[0]+=value.charAt(i);
               else text[1]+=value.charAt(i);
          }


          return text;
     }

     //left shift and permutation
     private String leftShiftAndPermutation(String lKey, String rKey)
     {
          StringBuilder leftKey = new StringBuilder();
          StringBuilder rightKey = new StringBuilder();

          try {
               for(int i=1;i<lKey.length();i++) {
                    leftKey.append(lKey.charAt(i));
               }

               leftKey.append(lKey.charAt(0));

               for(int i=1;i<rKey.length();i++) {
                    rightKey.append(rKey.charAt(i));
               }

               rightKey.append(rKey.charAt(0));
               String key = leftKey.append(rightKey).toString();

               return initialPermutation(key.substring(1,13));
          }catch (ArrayIndexOutOfBoundsException e)
          {
               dialogClass.error(e.toString());//if any error then show here
               return null;
          }
     }

     //xor here
     private List<String> xorString(String text,String key,int div)
     {
          List<String>binaryText;
          List<String>binaryKey;

          binaryText = toBinary(text);
          binaryKey = toBinary(key);

          List<String>binaryString = new ArrayList<>();

          int i=0,j=0;
          while (i<binaryText.size()&&j<binaryKey.size())
          {
               if(binaryText.get(i).equals(binaryKey.get(j)))
                    binaryString.add("0");//if equal then store 0 else store 1
               else binaryString.add("1");

               i++;
               j++;
          }

          List<String>binary = new ArrayList<>();//store binary in 6 or 4
          i=0;j=0;int count,index=0;
          while(i<binaryString.size()/div)
          {
               count =0;
               StringBuilder str = new StringBuilder();
               for(j=index;j<binaryString.size();j++)
               {
                    if(count==div)
                    {
                         index=j;
                         break;
                    }
                    str.append(binaryString.get(j));
                    count++;
               }
               binary.add(str.toString());
               i++;
          }
          return binary;
     }

     //last xor left text and right text
     private String xorLeftAndRight(String l,String r)
     {
          StringBuilder convertedValue = new StringBuilder();
          List<String>binary = xorString(l,r,4);
          for (int i=0;i<binary.size();i++)
               convertedValue.append(convertBinaryToHex(binary.get(i)));

          return convertedValue.toString();
     }

     //hexa to binary
     private List<String> toBinary(String value)
     {
          List<String> binary = new ArrayList<>();
          int i=0;
          while(i<value.length())
          {
               switch (value.charAt(i))
               {
                    case '0':
                         binary.add("0");binary.add("0");binary.add("0");binary.add("0");
                         break;
                    case '1':
                         binary.add("0");binary.add("0");binary.add("0");binary.add("1");
                         break;
                    case '2':
                         binary.add("0");binary.add("0");binary.add("1");binary.add("0");
                         break;
                    case '3':
                         binary.add("0");binary.add("0");binary.add("1");binary.add("1");
                         break;
                    case '4':
                         binary.add("0");binary.add("1");binary.add("0");binary.add("0");
                         break;
                    case '5':
                         binary.add("0");binary.add("1");binary.add("0");binary.add("1");
                         break;
                    case '6':
                         binary.add("0");binary.add("1");binary.add("1");binary.add("0");
                         break;
                    case '7':
                         binary.add("0");binary.add("1");binary.add("1");binary.add("1");
                         break;
                    case '8':
                         binary.add("1");binary.add("0");binary.add("0");binary.add("0");
                         break;
                    case '9':
                         binary.add("1");binary.add("0");binary.add("0");binary.add("1");
                         break;
                    case 'A':
                         binary.add("1");binary.add("0");binary.add("1");binary.add("0");
                         break;
                    case 'B':
                         binary.add("1");binary.add("0");binary.add("1");binary.add("1");
                         break;
                    case 'C':
                         binary.add("1");binary.add("1");binary.add("0");binary.add("0");
                         break;
                    case 'D':
                         binary.add("1");binary.add("1");binary.add("0");binary.add("1");
                         break;
                    case 'E':
                         binary.add("1");binary.add("1");binary.add("1");binary.add("0");
                         break;
                    case 'F':
                         binary.add("1");binary.add("1");binary.add("1");binary.add("1");
                         break;

               }
               i++;
          }
          return binary;
     }

     //get sbox value
     private String sBoxValue(String binaryString)
     {
          try {
               String index = Character.toString(binaryString.charAt(0)).concat(Character.toString(binaryString.charAt(binaryString.length()-1)));
               String binaryValue = binaryString.substring(1,binaryString.length()-1);
               int[] arrayIndex = sBoxRowCol(index,binaryValue);
               assert arrayIndex != null;
               return sBox[arrayIndex[0]][arrayIndex[1]];//its get value from sbox and return
          }catch (ArrayIndexOutOfBoundsException e)
          {
               dialogClass.error(e.toString());//if any error then show here
               return null;
          }
     }

     //find sbox row and column
     private int[] sBoxRowCol(String r,String c)
     {
          int row=0,col=0;
          try {
               if(r.charAt(0)=='0'&&r.charAt(1)=='0')
                    row=0;
               else if(r.charAt(0)=='0'&&r.charAt(1)=='1')
                    row=1;
               else if(r.charAt(0)=='1'&&r.charAt(1)=='0')
                    row=2;
               else if(r.charAt(0)=='1'&&r.charAt(1)=='1')
                    row=3;


               if(c.charAt(0)=='0'&&c.charAt(1)=='0'&&c.charAt(2)=='0'&&c.charAt(3)=='0')
                    col=0;
               else  if(c.charAt(0)=='0'&&c.charAt(1)=='0'&&c.charAt(2)=='0'&&c.charAt(3)=='1')
                    col=1;
               else  if(c.charAt(0)=='0'&&c.charAt(1)=='0'&&c.charAt(2)=='1'&&c.charAt(3)=='0')
                    col=2;
               else  if(c.charAt(0)=='0'&&c.charAt(1)=='0'&&c.charAt(2)=='1'&&c.charAt(3)=='1')
                    col=3;
               else  if(c.charAt(0)=='0'&&c.charAt(1)=='1'&&c.charAt(2)=='0'&&c.charAt(3)=='0')
                    col=4;
               else  if(c.charAt(0)=='0'&&c.charAt(1)=='1'&&c.charAt(2)=='0'&&c.charAt(3)=='1')
                    col=5;
               else  if(c.charAt(0)=='0'&&c.charAt(1)=='1'&&c.charAt(2)=='1'&&c.charAt(3)=='0')
                    col=6;
               else  if(c.charAt(0)=='0'&&c.charAt(1)=='1'&&c.charAt(2)=='1'&&c.charAt(3)=='1')
                    col=7;
               else  if(c.charAt(0)=='1'&&c.charAt(1)=='0'&&c.charAt(2)=='0'&&c.charAt(3)=='0')
                    col=8;
               else  if(c.charAt(0)=='1'&&c.charAt(1)=='0'&&c.charAt(2)=='0'&&c.charAt(3)=='1')
                    col=9;
               else  if(c.charAt(0)=='1'&&c.charAt(1)=='0'&&c.charAt(2)=='1'&&c.charAt(3)=='0')
                    col=10;
               else  if(c.charAt(0)=='1'&&c.charAt(1)=='0'&&c.charAt(2)=='1'&&c.charAt(3)=='1')
                    col=11;
               else  if(c.charAt(0)=='1'&&c.charAt(1)=='1'&&c.charAt(2)=='0'&&c.charAt(3)=='0')
                    col=12;
               else  if(c.charAt(0)=='1'&&c.charAt(1)=='1'&&c.charAt(2)=='0'&&c.charAt(3)=='1')
                    col=13;
               else  if(c.charAt(0)=='1'&&c.charAt(1)=='1'&&c.charAt(2)=='1'&&c.charAt(3)=='0')
                    col=14;
               else  if(c.charAt(0)=='1'&&c.charAt(1)=='1'&&c.charAt(2)=='1'&&c.charAt(3)=='1')
                    col=15;
               return new int[]{row,col};
          }catch (ArrayIndexOutOfBoundsException e)
          {
               dialogClass.error(e.toString());//if any error then show here
               return null;
          }
     }

     //convert 64 to 32 bit
     private String convertTo32Bit(String value)
     {
          StringBuilder convertValue = new StringBuilder();
          for(int i=0;i<value.length();i++)
          {
               if(i%2==0)
                    convertValue.append(value.charAt(i));
          }

          return initialPermutation(convertValue.toString());
     }

     //binary to hexa
     private String convertBinaryToHex(String c)
     {
          StringBuilder hex = new StringBuilder();

          if(c.charAt(0)=='0'&&c.charAt(1)=='0'&&c.charAt(2)=='0'&&c.charAt(3)=='0')
               hex.append("0");
          else  if(c.charAt(0)=='0'&&c.charAt(1)=='0'&&c.charAt(2)=='0'&&c.charAt(3)=='1')
               hex.append("1");
          else  if(c.charAt(0)=='0'&&c.charAt(1)=='0'&&c.charAt(2)=='1'&&c.charAt(3)=='0')
               hex.append("2");
          else  if(c.charAt(0)=='0'&&c.charAt(1)=='0'&&c.charAt(2)=='1'&&c.charAt(3)=='1')
               hex.append("3");
          else  if(c.charAt(0)=='0'&&c.charAt(1)=='1'&&c.charAt(2)=='0'&&c.charAt(3)=='0')
               hex.append("4");
          else  if(c.charAt(0)=='0'&&c.charAt(1)=='1'&&c.charAt(2)=='0'&&c.charAt(3)=='1')
               hex.append("5");
          else  if(c.charAt(0)=='0'&&c.charAt(1)=='1'&&c.charAt(2)=='1'&&c.charAt(3)=='0')
               hex.append("6");
          else  if(c.charAt(0)=='0'&&c.charAt(1)=='1'&&c.charAt(2)=='1'&&c.charAt(3)=='1')
               hex.append("7");
          else  if(c.charAt(0)=='1'&&c.charAt(1)=='0'&&c.charAt(2)=='0'&&c.charAt(3)=='0')
               hex.append("8");
          else  if(c.charAt(0)=='1'&&c.charAt(1)=='0'&&c.charAt(2)=='0'&&c.charAt(3)=='1')
               hex.append("9");
          else  if(c.charAt(0)=='1'&&c.charAt(1)=='0'&&c.charAt(2)=='1'&&c.charAt(3)=='0')
               hex.append("A");
          else  if(c.charAt(0)=='1'&&c.charAt(1)=='0'&&c.charAt(2)=='1'&&c.charAt(3)=='1')
               hex.append("B");
          else  if(c.charAt(0)=='1'&&c.charAt(1)=='1'&&c.charAt(2)=='0'&&c.charAt(3)=='0')
               hex.append("C");
          else  if(c.charAt(0)=='1'&&c.charAt(1)=='1'&&c.charAt(2)=='0'&&c.charAt(3)=='1')
               hex.append("D");
          else  if(c.charAt(0)=='1'&&c.charAt(1)=='1'&&c.charAt(2)=='1'&&c.charAt(3)=='0')
               hex.append("E");
          else  if(c.charAt(0)=='1'&&c.charAt(1)=='1'&&c.charAt(2)=='1'&&c.charAt(3)=='1')
               hex.append("F");

          return hex.toString();
     }

}
