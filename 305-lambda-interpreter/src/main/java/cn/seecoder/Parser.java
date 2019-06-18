package cn.seecoder;

import java.util.ArrayList;

public class Parser {
    Lexer lexer;

    public Parser(Lexer l){
        lexer = l;
    }

    public AST parse(){
        AST ast = term(new ArrayList<>());
//        System.out.println(lexer.skip(TokenType.EOF));
        return ast;
    }

    private AST term(ArrayList<String> ctx){
        String param;
        String paramValue;

        if (lexer.guessAndNext(TokenType.LAMBDA)){//判断现在的token是不是lambda，如果是，lexer自动更新，执行下面的，不是滚蛋。
            if (lexer.check(TokenType.LCID)){//判断现在的token是不是LCID，如果是，执行下面的。
                param=lexer.tokenvalue;//读取LCID的名称。
                lexer.skip(TokenType.LCID);//lexer更新。
                if (lexer.guessAndNext(TokenType.DOT)){//判断现在的token是不是DOT，如果是，la LCID .构成了抽象的组成结构,执行下面的。
                    ctx.add(0,param);//将这个抽象中的形式LCID名称加入字符串组的第一个。
                    paramValue=""+ctx.indexOf(param);//获得这个LCID的索引，转化为字符串。
                    AST aTerm=term(ctx);//继续从term角度构造语法树，获得这个抽象的函数体部分。
                    ctx.remove(ctx.indexOf(param));//将这个LCID从列表中移除。
                    return new Abstraction(new Identifier(param,paramValue),aTerm);//返回一个Abstraction语法树结构。
                }
            }
        }
        else return application(ctx);//如果第一个读到的并不是lambda，那就从application角度构造语法树。
        return null;
    }
    private AST application(ArrayList<String> ctx){//从application构造语法树分为左右两支
        AST lhs=atom(ctx);//左边一支
        AST rhs;
        while (true){
            rhs=atom(ctx);
            if (rhs==null) return lhs;
            else lhs = new Application(lhs, rhs);
        }
    }
    private AST atom(ArrayList<String> ctx){
        String param;
        String paramValue;
        if (lexer.guessAndNext(TokenType.LPAREN)){
            AST aTerm=term(ctx);
            if (lexer.guessAndNext(TokenType.RPAREN)){
                return aTerm;
            }
        }
        else if (lexer.check(TokenType.LCID)){
                param=lexer.tokenvalue;
                paramValue=""+ctx.indexOf(param);
                lexer.skip(TokenType.LCID);
                return new Identifier(param,paramValue);
            }
        return  null;
    }
}
