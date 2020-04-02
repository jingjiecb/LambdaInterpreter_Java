package cn.seecoder;

public class Abstraction extends AST {
    Identifier param;//变量
    AST body;//表达式

    Abstraction(Identifier p, AST b){
        param = p;//abstraction的要素之一，单一自变量。
        body = b;//abstraction的要素之二，函数体。
    }

    public String toString(){
        return "\\"+"."+body.toString();//可以调整来使其显示标识符。
    }

    /*
    public boolean equals(AST ast){
        if (ast instanceof Abstraction){
            if (this.body.equals(((Abstraction) ast).body))
                return true;
            else
                return false;
        }else return false;
    }

     */
}
