package cn.seecoder;


public class Lexer{

    public String source;
    public int index;//说明，这个index总是指向下一个token的开始，若对象储存的这个token是最后一个，token会指向一个越界的数，这时判定为EOF
    public TokenType token;
    public String tokenvalue;

    public Lexer(String s){
        index = 0;
        source = s;
        token=nextToken();
    }
    //get next token
    public TokenType nextToken(){

        //在范围内行事，如果index超出范围就返回EOF
        if (index<source.length()){
            StringBuilder string=new StringBuilder();

            //抽象出token
            switch (charAtIndex()){
                case '\\':index++;token=TokenType.LAMBDA;System.out.println(token);return token;
                case '.':index++;token=TokenType.DOT;System.out.println(token);return token;
                case '(':index++;token=TokenType.LPAREN;System.out.println(token);return token;
                case ')':index++;token=TokenType.RPAREN;System.out.println(token);return token;
                case ' ': index++; return nextToken();
                default:
                    if (('a'<=charAtIndex()&&charAtIndex()<='z')||('A'<=charAtIndex()&&charAtIndex()<='Z')){
                        string.append(charAtIndex());
                        index++;
                        for (;index<source.length();index++){
                            if (('a'<=charAtIndex()&&charAtIndex()<='z')||('A'<=charAtIndex()&&charAtIndex()<='Z')){
                                string.append(charAtIndex());
                            }
                            else break;
                        }
                        tokenvalue=string.toString();//将这个标识符抽象成LCID抽象词，并且将token的值设置成标识符
                        token=TokenType.LCID;
                        System.out.println(token);
                        return TokenType.LCID;
                    }
                    index++;
                    System.out.println(token);
                    return TokenType.EOF;//如果是非法字符也直接返回EOF
            }
        }

        //超出范围返回EOF
        else {
            index++;
            System.out.println(token);
            return TokenType.EOF;
        }
    }


    // get next char
    private char charAtIndex(){
        return source.charAt(index);//找到现在index对应的字符。
    }


    //check token == t
    public boolean next(TokenType t){//注：将原来的next方法名称改为check，方便理解。
        if (token==t) return true;//判断一下此时对象内部储存的token是不是函数参数中所给的token，然后不操作。
        else return false;
    }

    //assert matching the token type, and move next token
    public void match(TokenType t){//注：将原来的match方法名称改为skip，方面理解
        assert (token==t);//此时对象内部储存的token必须是参数所给的token，不然抛出错误。
        nextToken();//对象内部储存的token更新为下一个。
    }

    //skip token  and move next token
    public boolean skip(TokenType t){//注：将原来的skip方法名称改为guessAndNext，方便理解
        if (token==t){
            nextToken();//如果和假设中的token匹配，就返回true并自己更新为下一个。
            return true;
        }
        else {
            return false;//如果和假设的不同，返回false，不操作。
        }
    }
}
