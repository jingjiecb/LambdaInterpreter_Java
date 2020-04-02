package cn.seecoder;

import java.util.ArrayList;
import java.util.Scanner;

public class test {

    public static void main(String args[]) {

        Scanner input = new Scanner(System.in);
        String str = input.nextLine();
        Lexer test_lexer = new Lexer(str);
        //System.out.println(str);
        do {
            System.out.print(test_lexer.token);
            //if (test_lexer.next(TokenType.LCID)) System.out.println(":"+test_lexer.tokenvalue);
            //else System.out.println();
        }while (test_lexer.nextToken()!=TokenType.EOF);

        Parser test_parser=new Parser(new Lexer(str));
        System.out.println(test_parser.parse().toString());



        /*ArrayList<Integer> test=new ArrayList<>();
        test.add(1);
        test.add(2);
        test.add(0,2);

        int i;
        for (i=0;i<test.size();i++){
            System.out.println(test.get(i));
        }
        System.out.println(""+test.indexOf(2));

         */
    }
}
