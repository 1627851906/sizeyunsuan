import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Questions {
    public static int grade=0;
    private static int qNumber;
    private static Stack<Float> operatedNumber = new Stack<Float>();
    private static Stack<Character> operatedSign = new Stack<Character>();
    private static int signCount;
    private static char[] signArray = new char[]{'+', '-', '×', '÷','/', '(', ')', '='};
    private static char[][] signPriority = new char[][]{{'>', '>', '<', '<', '<','<', '>','>'},
            {'>', '>', '<', '<', '<', '<', '>','>'}, {'>', '>', '>', '>', '<', '<', '>','>'}, {'>', '>', '>', '>', '<', '<', '>','>'},
            {'>','>','>','>','>','>','>','>'},{'<', '<', '<', '<', '<','<', '=','>'}, {'>', '>', '>', '>', '<', '>', '>','>'},  {'<', '<', '<', '<', '<', '<', '<','<'}};
    private static StringBuilder expression = new StringBuilder();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("若答案为无理小数，则四舍五入保留两位小数，回答正确一题得一分");
        //setQuestionsNum();
//        printQuestions(qNumber);
        System.out.println("完成所有题目，你的得分为：" + grade);

    }

    private static int getPosition(Character sign) {
        for (int i = 0; i < signArray.length; i++) {
            if (sign == signArray[i]) {
                return i;
            }
        }
        return -1;
    }

    private static void setQuestionsNum() {
        System.out.print("请输入题目数：");
        Scanner in = new Scanner(System.in);
        boolean isVaild = false;
        while (!isVaild) {
            try{
                qNumber = in.nextInt();
                isVaild = true;
                if (qNumber<=0) {
                    System.out.println("输入题目数需大于或等于1");
                    isVaild = false;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("输入错误，请重新输入：");
                in.nextLine();
            }
        }
    }

    private static void setElements() {
        int index;
        int parent = 0;
        boolean flag = true;
        signCount = (int) (Math.random() * 4) + 1;
        operatedSign.add('=');
        for (int i = 0; i < signCount; i++) {
            if (flag) {
                index = (int) (Math.random() * 5);
            } else {
                index = (int) (Math.random() * 4);
            }
            if(flag && signArray[index] == '/') {
                operatedSign.add(signArray[index]);
                parent = (int) (Math.random() * 99) + 2;
                operatedNumber.add((float)parent);
                flag = false;
                addNumber(flag, parent);
                index = (int) (Math.random() * 4);
            } else {
                addNumber(true);
            }
            operatedSign.add(signArray[index]);
        }
        addNumber(true);
        addKuohao();
    }

    private static void addNumber(boolean flag,int... parent) {
        int number;
        if(!flag) {
            number = (int)(Math.random() * (parent[0]-1)) + 1;
            operatedNumber.add((float)number);
        }
        else {
            number = (int) (Math.random() * 100) + 1;
            operatedNumber.add((float)number);
        }
    }

    public static void addKuohao() {
        boolean flag = false;
        for (int i = operatedSign.size()-1; i > 1; i--) {
            if (operatedSign.get(i) == '÷' || operatedSign.get(i) == '×') {
                if (operatedSign.get(i-1) == '+' || operatedSign.get(i-1) == '-') {
                    operatedSign.add(i,'(');
                    flag = true;
                }
            }
            if (flag) {
                int index = (int)Math.random() * (i-1) + 1;
                operatedSign.add(index,')');
                break;
            }
        }
    }
    private static float calculate(Float number1, Character sign, Float number2) {
        switch (sign) {
            case '+':
                return number1 + number2;
            case '-':
                return number1 - number2;
            case '×':
                return number1 * number2;
            case '÷':
            case '/':
                return number1 / number2;
        }
        return -1;
    }

    private static Float getResult(Stack<Float> operatedNumber, Stack<Character> operatedSign) {
        int count = 0, flag=1;
        Stack<Float> tempNumber = new Stack<Float>();
        Stack<Character> tempSign = new Stack<Character>();
        Float newData = null, numberTop = null, numberNext = null, numberNextNext = null;
        Character signTop = null, signNext = null;
        while (operatedNumber.size() > 1 && operatedSign.get(operatedSign.size() - 1) != '=') {
            switch (signPriority[getPosition(operatedSign.get(operatedSign.size() - 1))][getPosition(operatedSign.get(operatedSign.size() - 2))]) {
                case '>':
                    numberTop = operatedNumber.pop();
                    numberNext = operatedNumber.pop();
                    signTop = operatedSign.pop();
                    newData = calculate(numberTop, signTop, numberNext);
                    operatedNumber.push(newData);
                    break;
                case '<':
                    numberTop = operatedNumber.pop();
                    numberNext = operatedNumber.pop();
                    numberNextNext = operatedNumber.pop();
                    signTop = operatedSign.pop();
                    signNext = operatedSign.pop();
                    if (!operatedSign.isEmpty()&& (signNext == '(' || operatedSign.get(operatedSign.size()-1)=='(')) {
                        if (operatedSign.get(operatedSign.size()-1)=='(') {
                            operatedSign.pop();
                            operatedNumber.push(numberNextNext);
                            flag=0;
                        }
                        else {
                            operatedNumber.add(numberNextNext);
                            operatedNumber.add(numberNext);
                        }
                        while(operatedSign.get(operatedSign.size()-1)!=')') {
                            tempSign.add(operatedSign.pop());
                            count++;
                        }
                        tempSign.push('=');
                        for (int i =0; i<count+1; i++) {
                            tempNumber.add(operatedNumber.pop());
                        }
                        tempNumber = setNumberReserve(tempNumber);
                        tempSign = setSignReserve(tempSign);
                        if (flag==1) {
                            newData = calculate(numberTop, signTop, getResult(tempNumber,tempSign));
                            operatedNumber.push(newData);
                        }
                        else {
                            newData = calculate(numberNext, signNext, getResult(tempNumber,tempSign));
                            operatedNumber.push(newData);
                            operatedNumber.push(numberTop);
                            operatedSign.push(signTop);
                        }
                    }
                    else {
                        newData = calculate(numberNext, signNext, numberNextNext);
                        operatedSign.push(signTop);
                        operatedNumber.push(newData);
                        operatedNumber.push(numberTop);
                    }
                    break;
                case '=':
                    operatedSign.pop();
                    operatedSign.pop();
                    break;
                default:
                    break;
            }
        }
        return operatedNumber.get(0);
    }

    public static void printQuestions(int qNumber) {
        Stack<Float> tempNumber = new Stack<Float>();
        Stack<Character> tempSign = new Stack<Character>();
        for (int i = 0; i < qNumber; i++) {
            setElements();
            tempNumber = (Stack<Float>) operatedNumber.clone();
            tempSign = (Stack<Character>) operatedSign.clone();
            if (getResult(tempNumber, tempSign)<0) {
                i--;
                reset();
                continue;
            }
           // System.out.print("第" + (i + 1) + "题 :");
            for (int k=operatedSign.size()-1,j = operatedNumber.size()-1; j > -1&&k>-1; j--,k--) {
                expression.append(operatedNumber.get(j).intValue());
                expression.append(operatedSign.get(k));
                if (k>0&&operatedSign.get(k-1)=='(') {
                    expression.append(operatedSign.get(k-1));
                    k--;
                }
                if(operatedSign.get(k)==')') {
                    expression.append(operatedSign.get(k-1));
                    k--;
                }
            }
            //System.out.print(expression);
            Main.setQuestion(expression.toString());
            Main.getAnswer(getResult(operatedNumber, operatedSign));
            //answerCheck(inputAnswer());
            expression.delete(0, expression.length());
            reset();
        }
    }

    private static Stack<Float> setNumberReserve(Stack<Float> number) {
        for(int i=number.size()-1,j=0; i>=j; i--,j++) {
            Float temp1 = number.get(i);
            Float temp2 = number.get(j);
            number.set(i,temp2);
            number.set(j,temp1);
        }
        return number;
    }
    private static Stack<Character> setSignReserve(Stack<Character> sign) {
        for(int i=sign.size()-1,j=0; i>=j; i--,j++) {
            Character temp1 = sign.get(i);
            Character temp2 = sign.get(j);
            sign.set(i,temp2);
            sign.set(j,temp1);
        }
        return sign;
    }
    public static Float inputAnswer(String in) {
        String answer = in;
        String pattern = "(\\d+)/(\\d+)";
       // Scanner in = new Scanner(System.in);
        boolean isVaild = false;
        while (!isVaild) {
            try {
                //answer = in.nextLine();
                isVaild = true;
                Pattern p = Pattern.compile(pattern);
                Matcher matcher = p.matcher(answer);
                if (matcher.find()) {
                    return Float.parseFloat(matcher.group(1)) / Float.parseFloat(matcher.group(2));
                }
                else {
                    return Float.parseFloat(answer);
                }
            }
            catch (Exception e) {
                return (float)-1;
                //System.out.println("输入错误！请重新输入");
                //in.nextLine();
            }
        }
        return Float.parseFloat("0");
    }
    public static String answerCheck(Float questionAnswer, Float myAnswer) {
        String messege = "";
        DecimalFormat df = new DecimalFormat("0.00");
        if (Math.abs(Float.parseFloat(df.format(myAnswer)) - Float.parseFloat(df.format(questionAnswer)))<=0) {
            messege = "答案正确!";
            grade++;
            return messege;
        }
        else {
            messege = "答案错误，正确答案为：" + df.format(questionAnswer);
            return messege;
        }
    }
    private static void reset() {
        while(!operatedSign.isEmpty()) {
            operatedSign.pop();
        }
        while(!operatedNumber.isEmpty()) {
            operatedNumber.pop();
        }
    }
}

