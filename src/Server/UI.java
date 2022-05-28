package Server;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;

/*public class UI {

  //  public static void main(String[] args) {
       public static void jiemian(String[] values){
        // TODO Auto-generated method stub
        Frame frame = new Frame("基本组件测试");
        frame.setBounds(100, 100, 600, 300);
        GridLayout gl = new GridLayout(4,2,5,5); //设置表格为3行两列排列，表格横向间距为5个像素，纵向间距为5个像素
        frame.setLayout(gl);

        //按钮组件
        Button but1 = new Button("测试按钮");
        Panel pn0 = new Panel();
        pn0.setLayout(new FlowLayout());
        pn0.add(but1);
        frame.add(pn0);

        //复选框组件
        Panel pn1 = new Panel();
        pn1.setLayout(new FlowLayout());
        pn1.add(new Checkbox("one",null,true));
        pn1.add(new Checkbox("two"));
        pn1.add(new Checkbox("three"));
        frame.add(pn1);

        //复选框组（单选）
        Panel pn2 = new Panel();
        CheckboxGroup cg = new CheckboxGroup();
        pn2.setLayout(new FlowLayout());
        pn2.add(new Checkbox("one",cg,true));
        pn2.add(new Checkbox("two",cg,false));
        pn2.add(new Checkbox("three",cg,false));
        frame.add(pn2);

        //下拉菜单
        Choice cC = new Choice();
        cC.add("red");
        cC.add("green");
        cC.add("yellow");
        frame.add(cC);

        //单行文本框
        Panel pn3 = new Panel();
        pn3.setLayout(new FlowLayout());
        TextField tf = new TextField("",30); //30列长度
        pn3.add(tf);
        frame.add(pn3);

        //多行文本框
        TextArea ta = new TextArea();
        frame.add(ta);

        //列表
        List ls = new List();
        ls.add("a");
        ls.add("b");
        ls.add("c");
        ls.add("d");
        frame.add(ls);
        frame.setVisible(true);
    }


}*/

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class UI extends JFrame{

 //三个组件
 private JButton saveButton;
 private JButton loadButton;
 private TextArea textArea;

 //容器
 private Container container;

 public UI() {
  //设置title
  super("File Demo");

  //设置流布局
  setLayout(new FlowLayout());

  //获取容器
  container = getContentPane();

  //三个组件
  textArea = new TextArea();
  saveButton = new JButton("save");
  loadButton = new JButton("load");

  //保存文件按钮点击事件
  saveButton.addActionListener(new ActionListener() {

   @Override
   public void actionPerformed(ActionEvent e) {

    System.out.println("存档成功");
   }
  });

  //读入文件按钮点击事件
  loadButton.addActionListener(new ActionListener() {

   @Override
   public void actionPerformed(ActionEvent e) {

    System.out.println("读档成功");
   }
  });

  //装填三个组件
  container.add(textArea);
  container.add(loadButton);
  container.add(saveButton);

  //调整大小
  setSize(500, 300);
  //显示
  setVisible(true);
 }

 public static void main(String[] args) {
  UI demo = new UI();
  demo.setDefaultCloseOperation(EXIT_ON_CLOSE);
 }
}

