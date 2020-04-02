package cn.seecoder;

import java.util.ArrayList;

public class Parser {
    Lexer lexer;

    public Parser(Lexer l){
        lexer = l;
    }

    public AST parse(){
        AST ast = term(new ArrayList<>());
//        System.out.println(lexer.match();(TokenType.EOF));
        return ast;
    }

    private AST term(ArrayList<String> ctx){
        String param;
        String paramValue;

        if (lexer.skip(TokenType.LAMBDA)){//判断现在的token是不是lambda，如果是，lexer自动更新，执行下面的，不是跳过。
            if (lexer.next(TokenType.LCID)){//判断现在的token是不是LCID，如果是，执行下面的。
                param=lexer.tokenvalue;//读取LCID的名称。
                lexer.match(TokenType.LCID);//lexer更新。
                if (lexer.skip(TokenType.DOT)){//判断现在的token是不是DOT，如果是，la LCID .构成了抽象的组成结构,执行下面的。
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
        AST rhs;//右边一支
        while (true){
            rhs=atom(ctx);
            if (rhs==null) return lhs;
            else lhs = new Application(lhs, rhs);
        }
    }
    private AST atom(ArrayList<String> ctx){//application的每一支都通过atom方法进行分析
        String param;
        String paramValue;
        if (lexer.skip(TokenType.LPAREN)){//第一种情况，这是一个括号括起来的整体
            AST aTerm=term(ctx);//剥离括号，并且把里面的内容用一开始的方法重新分析。
            if (lexer.skip(TokenType.RPAREN)){
                return aTerm;
            }
        }
        else if (lexer.next(TokenType.LCID)){//第二种情况，这个atom是一个标识符
                param=lexer.tokenvalue;//记录标识符的名字
                paramValue=""+ctx.indexOf(param);//记录标识符的带入层值
                lexer.match(TokenType.LCID);//检查
                return new Identifier(param,paramValue);//返回标识符
            }
        return  null;
    }
}
