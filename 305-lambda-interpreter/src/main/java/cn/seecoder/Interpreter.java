package cn.seecoder;

public class Interpreter {
    Parser parser;
    AST astAfterParser;

    public Interpreter(Parser p){
        parser = p;
        astAfterParser = p.parse();
        //System.out.println("After parser:"+astAfterParser.toString());
    }


    //下面的三个函数用来判断ast的具体类型。
    private  boolean isAbstraction(AST ast){
        return ast instanceof Abstraction;
    }
    private  boolean isApplication(AST ast){
        return ast instanceof Application;
    }
    private  boolean isIdentifier(AST ast){
        return ast instanceof Identifier;
    }


    public AST eval(){

        return evalAST(astAfterParser);
    }

    private   AST evalAST(AST ast){
        while (true){

            if (isApplication(ast)){//如果整个最顶层是应用，
                Application ap=(Application)ast;
                if (isApplication(ap.lhs)){//如果左支也是应用，
                    ap.lhs=evalAST(ap.lhs);//把左支求值。
                    if (isApplication(ap.lhs)){//如果左支求值之后还是应用，
                        return ast;//说明已经无法继续求值了，直接返回现在的状况。
                    }
                }
                else if (isAbstraction(ap.lhs)){//如果左支是抽象，
                    if (isApplication(ap.rhs)){//如果右支是应用，
                        ap.rhs=evalAST(ap.rhs);//那就先把应用求值，准备往左边带入,
                    }
                    ast=substitute(((Abstraction)ap.lhs).body,ap.rhs);//代入。
                }
                else {
                    ((Application) ast).rhs = evalAST(((Application) ast).rhs);
                    return ast;
                }
            }
            else if (isAbstraction(ast)){//如果是抽象，
                Abstraction abs=((Abstraction)ast);
                abs.body=evalAST(abs.body);//对抽象的函数体进行求值。
                return ast;
            }
            else if (isIdentifier(ast)){//如果是标识符，
                return ast;//直接返回。
            }
            else return null;
        }
    }
    private AST substitute(AST node,AST value){//将value的一支代入节点处。

        //System.out.println("node:"+node.toString()+"  value:"+value.toString());
        AST result=shift(-1,subst(node,shift(1,value,0),0),0);
        //System.out.println(result.toString());
        return result;
        //return shift(-1,subst(node,shift(1,value,0),0),0);
    }

    /**
     *  value替换node节点中的变量：
     *  如果节点是Applation，分别对左右树替换；
     *  如果node节点是abstraction，替入node.body时深度得+1；
     *  如果node是identifier，则替换De Bruijn index值等于depth的identifier（替换之后value的值加深depth）

     *@param value 替换成为的value
     *@param node 被替换的整个节点
     *@param depth 外围的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */
    private AST subst(AST node, AST value, int depth){//value表示要代入的ast，node表示要代入的节点。
        if(isApplication(node)){
            return new Application(subst(((Application) node).lhs,value,depth),subst(((Application) node).rhs,value,depth));
        }
        else if(isAbstraction(node)){
            return new Abstraction(((Abstraction) node).param,subst(((Abstraction) node).body,value,depth+1));
        }
        else{
            if(depth==Integer.parseInt(((Identifier)node).value)) return shift(depth,value,0);
            else return node;
        }
    }

    /**

     *  De Bruijn index值位移
     *  如果节点是Applation，分别对左右树位移；
     *  如果node节点是abstraction，新的body等于旧node.body位移by（from得+1）；
     *  如果node是identifier，则新的identifier的De Bruijn index值如果大于等于from则加by，否则加0（超出内层的范围的外层变量才要shift by位）.

        *@param by 位移的距离
     *@param node 位移的节点
     *@param from 内层的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */

    private AST shift(int by, AST node,int from){
        if(isApplication(node)){
            return new Application(shift(by,((Application)(node)).lhs,from), shift(by,((Application)(node)).rhs,from));
        }
        else if(isAbstraction(node)){
            return new Abstraction(((Abstraction) node).param,shift(by,((Abstraction) node).body,from+1));
        }
        else{
            return new Identifier(((Identifier) node).name, String.valueOf(Integer.parseInt(((Identifier) node).value) + (Integer.parseInt(((Identifier) node).value) >= from ? by : 0)));
        }
    }

    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m "+SUCC+") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n"+PRED+"m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x."+FALSE+")"+TRUE+")";
    static String LEQ = "(\\m.\\n."+ISZERO+"("+SUB+"m n))";
    static String EQ = "(\\m.\\n."+AND+"("+LEQ+"m n)("+LEQ+"n m))";
    static String MAX = "(\\m.\\n."+IF+"("+LEQ+" m n)n m)";
    static String MIN = "(\\m.\\n."+IF+"("+LEQ+" m n)m n)";

    private static String app(String func, String x){
        return "(" + func + x + ")";
    }
    private static String app(String func, String x, String y){
        return "(" +  "(" + func + x +")"+ y + ")";
    }
    private static String app(String func, String cond, String x, String y){
        return "(" + func + cond + x + y + ")";
    }

    public static void main(String[] args) {
        // write your code here


        String[] sources = {
                ZERO,//0
                ONE,//1
                TWO,//2
                THREE,//3
                app(PLUS, ZERO, ONE),//4
                app(PLUS, TWO, THREE),//5
                app(POW, TWO, TWO),//6
                app(PRED, ONE),//7
                app(PRED, TWO),//8
                app(SUB, FOUR, TWO),//9
                app(AND, TRUE, TRUE),//10
                app(AND, TRUE, FALSE),//11
                app(AND, FALSE, FALSE),//12
                app(OR, TRUE, TRUE),//13
                app(OR, TRUE, FALSE),//14
                app(OR, FALSE, FALSE),//15
                app(NOT, TRUE),//16
                app(NOT, FALSE),//17
                app(IF, TRUE, TRUE, FALSE),//18
                app(IF, FALSE, TRUE, FALSE),//19
                app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
                app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
                app(ISZERO, ZERO),//22
                app(ISZERO, ONE),//23
                app(LEQ, THREE, TWO),//24
                app(LEQ, TWO, THREE),//25
                app(EQ, TWO, FOUR),//26
                app(EQ, FIVE, FIVE),//27
                app(MAX, ONE, TWO),//28
                app(MAX, FOUR, TWO),//29
                app(MIN, ONE, TWO),//30
                app(MIN, FOUR, TWO),//31
        };

        for(int i=0 ; i<sources.length; i++) {

            Lexer test_lexer = new Lexer(sources[i]);
            //System.out.println(str);

            /*
            do {
                System.out.print(test_lexer.token);
                System.out.println();
            }while (test_lexer.nextToken()!=TokenType.EOF);

             */

            String source = sources[i];

            System.out.println(i+":"+source);

            Lexer lexer = new Lexer(source);

            Parser parser = new Parser(lexer);

            Interpreter interpreter = new Interpreter(parser);

            AST result = interpreter.eval();

            System.out.println(i+":" + result.toString());

        }

    }
}