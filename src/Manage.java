import java.util.Random;
import javax.swing.*;

public class Manage extends Thread{
	PCB pcb[];
	requireBlock requireblock[];
	int buffer[][];
	int outputBufferSpace[];		    //可使用的输出井buffer空间 
	int outputBufferPointer[][]; 	  //输出井buffer空闲和满指针
	int requireblockNumber;      	  //requireblock的剩余数量
	int requireblockOutputPointer; 	//要输出的第一个reqblock指针 
	int requireblockFreePointer; 	  //第一个空闲reqblock指针
	double random;     				      //用于调度三个进程的控制随机数 
	int output1; 					          //用户进程1已生成的文件数 
	int output2; 					          //用户进程2已生成的文件数 
	int output_1; 					        //用户进程1已输出的文件数
	int output_2; 					        //用户进程2已输出的文件数
	int x;  		 				            //随机生成的数据0～9 
	int i;   						            //临时控制变量
	Random x1;  					          //辅助生成随机数据x：0～9  
	Window window; 

	/**
	 * 这个方法是对两个进程进行数据初始化
	 * @param window
	 */
	public Manage( Window window ){
		output1 = 0;
		output2 = 0;
		output_1 = 0;
		output_2 = 0;

		pcb = new PCB[4];
		requireblock = new requireBlock[10];
		buffer = new int[3][100];
		outputBufferSpace = new int[3];
		outputBufferSpace[1] = 100;
		outputBufferSpace[2] = 100;

		outputBufferPointer = new int[3][2];
		outputBufferPointer[1][0] = 0;
		outputBufferPointer[2][0] = 0;

		requireblockNumber = 10;

		requireblockOutputPointer = 0;
		requireblockFreePointer = 0;

		x1 = new Random();
		for( i = 0; i < 4; i++ ){
			pcb[ i ] = new PCB();
		}

		for( i = 0; i < 10; i++ ){
			requireblock[ i ] = new requireBlock();
		}

		for( i = 1; i <=3; i++ ){
			pcb[ i ].status = 0;
		}

		this.window = window;	//对各进程的数据初始化完毕  把这个传过来有助于更新界面内容
	}

	public void run(){ //进程调度 
		do{ //while循环

			random = Math.random();//产生一个随机数，控制进程调度，令用户进程概率为45%，Spooling进程为10% 
			if( random <= 0.45 &&  pcb[ 1 ].status == 0 ){ //调度用户进程1
				window.jta4.append( "调度用户进程1\n" );

				try{
					sleep( 500 );
				}
				catch( InterruptedException e ){
					e.printStackTrace();
				}
				///调用用户函数去生成文件填入输出井生成请求块
				output1 = user( 1, output1, window.jta1, window.jtf1 );
			}
			else if( random >0.45 && random <= 0.9 && pcb[ 2 ].status == 0 ){ //调度用户进程2
				window.jta4.append( "调度用户进程2\n" );

				try{
					sleep( 500 );
				}
				catch( InterruptedException e ){
					e.printStackTrace();
				}
				output2 = user( 2, output2, window.jta2, window.jtf2 );
			}
			else if( random >0.9 && random <= 1 && pcb[3].status == 0 ){ //调度spooling进程
				window.jta4.append("调度Spooling进程\n");  

				try{
					sleep( 500 );
				}
				catch( InterruptedException e ){
					e.printStackTrace();
				}
				spooling1();
			}
		}
		while( pcb[1].status != 4 || pcb[2].status != 4 || pcb[3].status != 4 ); // 当都是结束状态曾程序运行完成 
		window.jta4.append("程序运行完毕\n"); //进程调度结束 
	}

	public int user( int name, int out, JTextArea jta, JTextField field ){ //用户进程  返回已经生成的文件数目
		pcb[ name ].ID = name;
		pcb[ name ].outputFileCount = Integer.parseInt( field.getText() );
		while( out != pcb[ name ].outputFileCount ){//判断进程所要输出的文件是否输出完毕的while循环 

			outputBufferPointer[ name ][ 1 ] = outputBufferPointer[ name ][ 0 ];

			do{ //判断进程的一个文件是否输出完毕的while循环 
				x = x1.nextInt( 9 );//x为每次随机生成的数据0～9，送入pcb.x 
				pcb[ name ].outPut_X = x;

				if( outputBufferSpace[ name ] == 0 ){ //若输出井buffer满，变为等待状态1，转调度程序
					pcb[ name ].status = 1;
					//一个文件的长度是未知的在创建的过程中如果发现井满了要退回去

					if( outputBufferPointer[ name ][ 0 ] >= outputBufferPointer[ name ][1] ){
						outputBufferSpace[ name ] = outputBufferSpace[ name ] + outputBufferPointer[ name ][ 0] - outputBufferPointer[ name ][ 1 ];//将空间释放
					}
					else{
						outputBufferSpace[ name ] = outputBufferSpace[ name ] + 100 + outputBufferPointer[ name ][ 0] + outputBufferPointer[ name ][ 1 ];
					}
					outputBufferPointer[ name ][ 0 ] = outputBufferPointer[ name ][ 1 ]; //将空间释放
					jta.append( "第" + ( out + 1 ) + "个文件缺少输出井" );
					jta.append( "进入等待状态1\n" );

					try{
						sleep( 500 );
					}
					catch( InterruptedException e ){
						e.printStackTrace();
					}
					return out;
				}
				else{//若输出井没满

					buffer[ name ][ outputBufferPointer[ name ][ 0 ] ] = pcb[ name ].outPut_X;  //进程的输出信息PCB[i].x送buffer[i][ outputBufferPointer[i][0]]  
					outputBufferSpace[ name ] = outputBufferSpace[ name ] - 1; //输出井空闲个数减1
					outputBufferPointer[ name ][ 0 ] = ( outputBufferPointer[ name ][ 0 ] + 1 ) % 100;  //修改空缓冲区指针outputBufferPointer[i][0]前进1
				}
			}
			while( x != 0 ); //判断进程的一个文件是否输出完毕的while循环结束

			if( outputBufferPointer[ name ][ 0 ] == 0 ){
				jta.append( (out + 1)  + " " +  outputBufferPointer[ name ][ 1 ] + " ~ " + " 99 " + outputBufferSpace[ name ] + " "); 
			}
			else{
				jta.append( (out + 1)  + " " +  outputBufferPointer[ name ][ 1 ] + " ~ " + ( outputBufferPointer[ name ][ 0 ] - 1 ) + " " + outputBufferSpace[ name ] + " " ); 
			}
			try{
				sleep( 500 );
			}
			catch( InterruptedException e ){
				e.printStackTrace();
			}
			out++; //成功生成了一个文件

			//接下来要生成相应的请求块

			if( requireblockNumber == 0 ){//若没有空闲请求输出块，转为等待状态3  

				pcb[ name ].status = 3;
				jta.append( "缺少请求输出块" );
				jta.append( "进入等待状态3 \n" );

				try{
					sleep( 500 );
				}
				catch( InterruptedException e ){
					e.printStackTrace();
				}
				return out;
			}
			else{ //若有空闲请求输出块 

				requireblock[ requireblockFreePointer ].outputHeadAddress = outputBufferPointer[ name ][ 1 ]; //将文件在输出井的位置填入空闲请求块

				if( outputBufferPointer[ name ][ 0 ] >= outputBufferPointer[ name ][ 1 ] ){ //将文件在输出井的长度填入空闲请求块
					requireblock[ requireblockFreePointer ].length = outputBufferPointer[ name ][ 0 ] - outputBufferPointer[ name ][ 1 ]; 
				} 
				else{
					requireblock[ requireblockFreePointer ].length = 100 - outputBufferPointer[ name ][ 1 ] + outputBufferPointer[ name ][ 0 ];
				}
				requireblock[ requireblockFreePointer ].requireName = name; //将进程名i填入请求块
				jta.append( "获得请求输出块" + Integer.toString( requireblockFreePointer +1 )  + "\n");
				requireblockFreePointer = ( requireblockFreePointer + 1 ) % 10;//修改空闲请求块指针 
				requireblockNumber--;

				if( pcb[ 3 ].status == 2 ){ //若SPOOLING进程是等待状态，则唤醒SPOOLING进程 

					pcb[ 3 ].status = 0;
				}
			}

		}//判断进程所要输出的文件是否输出完毕的while循环结束 

		jta.append( "进程" + name + "输出完毕！" );//文件输出完毕，修改状态为结束，转进程调度
		pcb[ name ].status = 4;
		return out; 
	}

	public void spooling1(){

		while( requireblockNumber != 10 ){ //判断请求输出块是否为空的while循环 
			//若请求输出块不为空

			StringBuffer stringBuffer = new StringBuffer( 100 );
			for ( i = 0; i<requireblock[ requireblockOutputPointer ].length; i++) {//按该请求输出信息块requireblock[]的指针requireblockOutputPointer将输出井中的一个文件的内容放入临时buffer1中 
				stringBuffer.append( buffer[ requireblock[ requireblockOutputPointer ].requireName ][ requireblock[ requireblockOutputPointer ].outputHeadAddress] );
				requireblock[ requireblockOutputPointer ].outputHeadAddress = ( requireblock[ requireblockOutputPointer ].outputHeadAddress + 1 )%100;
			}
			if( requireblock[ requireblockOutputPointer ].requireName == 1){
				output_1++;
				window.jta3.append( "User1第" + output_1 + "个文件:");
			}
			else{
				output_2++;
				window.jta3.append( "User2第" + output_2 + "个文件:");
			}
			window.jta3.append( stringBuffer.toString() + "\n" );

			try{
				sleep( 500 );
			} 
			catch( InterruptedException e ){
				e.printStackTrace();
			}

			//释放相应输出井，即修改相应的输出井计数outputBufferSpace
			outputBufferSpace[ requireblock[ requireblockOutputPointer ].requireName ] = outputBufferSpace[ requireblock[ requireblockOutputPointer ].requireName ] + requireblock[ requireblockOutputPointer ].length;
			requireblockOutputPointer = ( requireblockOutputPointer + 1 )%10;
			requireblockNumber++;

			for( int k = 1; k <= 2; k++ ){
				if( pcb[ k ].status == 3 ){ //有等待请求输出块的进程，唤醒没有请求块的进程   一次输出进程完成只能解禁一个请求块所以要完成一个就切断
					//应该看先是否有等待请求块的进程，这样可以尽早释放相应的井空间
					pcb[ k ].status = 0;
					requireblock[ requireblockFreePointer ].outputHeadAddress = outputBufferPointer[ k ][ 1 ];//将文件在输出井的位置填入空闲请求块 
					if( outputBufferPointer[ k ][ 0 ] >= outputBufferPointer[ k ][ 1 ] ){ //将文件在输出井的长度填入空闲请求块 
						requireblock[ requireblockFreePointer ].length = outputBufferPointer[ k ][ 1 ];
					}
					else{
						requireblock[ requireblockFreePointer ].length = 100 - outputBufferPointer[ k ][ 1] + outputBufferPointer[ k ][ 0 ];
					}
					requireblock[ requireblockFreePointer ].requireName = k;//将进程名i填入请求块

					if( k ==1 ){
						window.jta1.append("第" + output1 + "个文件获得请求输出块" + ( Integer.toString( requireblockFreePointer + 1 ) ) + "\n" ); 
					}
					if( k == 2 ){
						window.jta2.append("第" + output2 + "个文件获得请求输出块" + ( Integer.toString( requireblockFreePointer + 1 ) ) + "\n" );
					}
					requireblockFreePointer = ( requireblockFreePointer +1 )%10;//修改空闲请求块指针 
					requireblockNumber--; //空闲请求块数减1

					if( pcb[ 3 ].status == 2 ){ //若SPOOLING进程是等待状态，则唤醒SPOOLING进程
						pcb[ 3 ].status = 0;
					}
					return; //完成一个就切断, 可能两个都等待请求块,但先满足一个吧  
				}
			}

			for( int k =1; k <=2; k++ ){

				if( pcb[ k ].status ==1 ){ //有等待输出井的进程，唤醒相应进程
					pcb[ k ].status = 0;
					return;
				}
			}
		}//判断请求输出块是否为空的while循环结束 

		if( pcb[ 1 ].status ==4 && pcb[ 2 ].status ==4 ){//进程1、2结束后输出进程结束 
			pcb[ 3 ].status = 4;
			window.jta3.append( " Spooling输出进程结束" );
			return;
		}
		else{
			window.jta3.append( "调度spooling输出程序，请求块空，输出进程转入状态2\n" );
			pcb[ 3 ].status = 2;
		}
	}
}
