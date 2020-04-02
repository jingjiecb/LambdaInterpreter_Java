package cn.seecoder;

public class Identifier extends AST {

    String name; //名字
    String value;//De Bruijn index值

    public Identifier(String n,String v){//构造函数
        name = n;
        value = v;
    }
    public String toString(){
        return value;//可以修改此处的返回值来决定要返回index值还是名称。
    }

    /*
    public boolean equals(AST ast){
        if(ast instanceof Identifier) {
            if (this.value.equals(((Identifier) ast).value))
                return true;
            else
                return false;
        }else return false;
    }

     */
}
