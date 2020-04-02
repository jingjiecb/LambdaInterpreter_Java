package cn.seecoder;

public class Application extends AST{
    AST lhs;//左树
    AST rhs;//右树

    Application(AST l, AST s){
        lhs = l;
        rhs = s;
    }

    public String toString(){
        if (lhs==null){
            return rhs.toString();
        }
        else if (rhs==null){
            return lhs.toString();
        }
        else if (rhs!=null&&lhs!=null){
            return "("+lhs.toString()+" "+rhs.toString()+")";
        }
        else return "";
    }

    /*
    public boolean equals(AST ast){
        if (ast instanceof Application){
            if (this.lhs.equals(((Application)ast).lhs)&&this.rhs.equals(((Application)ast).rhs))
                return true;
            else
                return false;
        }else return false;
    }

     */
}
