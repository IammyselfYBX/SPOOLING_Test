/**
 * @author:杨秉学
 * Time:2020,6,24
 * Function:
 * 		1.负责窗体的布局UI设计
 * 		2.进行事件响应
 * @version 1.2
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.reflect.Field;

public class Window extends JFrame implements ActionListener, Runnable{
	JPanel jp1, jp2, jp3;
	JTextField jtf1, jtf2;
	JScrollPane p1, p2, p3, p4;
	JTextArea jta1, jta2, jta3, jta4;
	JButton btn1, btn2, btn3;
//	Manage manage;

	/**
	 * 界面构造函数
	 */
	public Window(){	
		Container container = this.getContentPane();
		container.setLayout( new BorderLayout() );

		/**
		 * 设置panel1
		 */
		jtf1 = new JTextField( 3 );
		jtf2 = new JTextField( 3 );

		btn1 = new JButton( "开始运行" );
		btn1.addActionListener( this );
		btn2 = new JButton( "退出程序" );
		btn2.addActionListener( this );
		btn3 = new JButton( "重新开始" );
		btn3.addActionListener( this );

		jp1 = new JPanel();
		jp1.setLayout( new FlowLayout() );

		jp1.add( new JLabel( "User Process1 文件数：", SwingConstants.RIGHT ) );
		jp1.add( jtf1 );
		jp1.add( new JLabel( "User Process2 文件数：", SwingConstants.RIGHT) );
		jp1.add( jtf2 );
		jp1.add( btn1 );
		jp1.add( btn2 );
		jp1.add( btn3 );

		/**
		 * 设置panel2
		 */
		
		jta1 = new JTextArea( 80,100 );//设置panel2
		jta2 = new JTextArea( 112,400 );

		jta1.append( "用户进程1的文件生成情况及状态变换\n文件序号	位    置	剩余空间	状态\n" );
		jta2.append("用户进程2的文件生成情况及状态变换\n文件序号	位    置	剩余空间	状态\n");

		p1 = new JScrollPane( jta1 );
		p2 = new JScrollPane( jta2 );

		jp2 = new JPanel();
		jp2.setLayout ( new GridLayout( 1,2 ) );
		jp2.add( p1 );
		jp2.add( p2 );

		/**
		 * 设置panel3
		 */
		jta3 = new JTextArea( 15,400 );//设置panel3
		jta4 = new JTextArea( 15,150 );
		jta3.append( "Spooling调度输出\n" );
		jta4.append("主程序调度\n");

		p3 = new JScrollPane( jta3 );
		p4 = new JScrollPane( jta4 );

		jp3 = new JPanel();
		jp3.setLayout( new GridLayout( 1,2 ) );
		jp3.add( p3 );
		jp3.add( p4 );

		container.add( jp1, BorderLayout.NORTH );//设置窗口
		container.add( jp2 ,BorderLayout.CENTER );
		container.add( jp3, BorderLayout.SOUTH );

		this.setSize( 1200,600 );
		this.setLocation( 100,100 );
		this.setTitle( "SPOOLING假脱机输入输出技术模拟" );
		this.setVisible( true );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );//设置窗口完毕
	}

	public void actionPerformed( ActionEvent e ){
     if(e.getSource() == btn1)
     {
        run();
     }
     if(e.getSource() == btn2)
     {
        System.exit(-1);
     }
     if(e.getSource() == btn3)
     {
        begin();
     }
	}

  public void run()
  {
		 Manage manage = new Manage(this);
     manage.start();
  }

  public void begin()
  {
     jtf1.setText("");
     jtf2.setText("");
		 jta1.setText( "用户进程1的文件生成情况及状态变换\n文件序号	位    置	剩余空间	状态\n" );
		 jta2.setText("用户进程2的文件生成情况及状态变换\n文件序号	位    置	剩余空间	状态\n");
		 jta3.setText( "Spooling调度输出\n" );
		 jta4.setText("主程序调度\n");
  }
}
