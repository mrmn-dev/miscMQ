/*****************************************************************************/
/* Program name: amqbdlh derivative of IBM sample amqsbcg                    */
/* Description: browse dead letter queue messages                            */
/*                                                                           */
/* This program is run as follows:                                           */
/*                                                                           */
/*     java amqbdlh -qm ... -q ...                                           */
/*                                                                           */
/* where                                                                     */
/*     -qm is the queue manager name                                         */
/*     -q  is the queue with dead letter messages                            */
/*                                                                           */
/* An alternate method is to run the program with an amqbdlh.properties file */
/* that contains the qm and q definitions:                                   */
/*                                                                           */
/*    qm=my.qmgr                                                             */
/*    q=my.dead.letter.queue                                                 */
/*                                                                           */
/*****************************************************************************/
import java.lang.*;
import java.io.*;
import java.util.Properties; 
/* import java.util.ResourceBundle; */ /* uncomment if using ResourceBundle */
                                       /* to get program's input            */
import java.util.Calendar;
import com.ibm.mq.*;
import com.ibm.mq.headers.MQDLH;
import com.ibm.mq.headers.MQDataException;

public class amqbdlh {

   private static String    myQmgr  = null;
   private static String    myQueue = null;
   MQDataException dex = null;

   /*******************************************************/
   /* Default constructor to read the properties file for */
   /* the qm and q parameters.                            */
   /*******************************************************/
   public amqbdlh() {

      /***********************************************/
      /* We'll get anything with the -D syntax first */
      /***********************************************/
      myQmgr  = System.getProperty("qm");
      myQueue = System.getProperty("q");

      /*************************************************/
      /* Now go and get the amqbdlh.properties file.   */
      /*************************************************/
      try {
         Properties props = new Properties(System.getProperties());
         props.load(new BufferedInputStream(new FileInputStream("amqbdlh.properties")));
         System.setProperties(props);
      } catch (Exception e) {
         System.out.println("Error getting amqbdlh.properties: " + e.getMessage());
         System.err.println(e);
      }
 
      /******************************************************/
      /* If any of the necessary properties are null, we'll */
      /* take the value from the file since they weren't    */
      /* specified with -D.                                 */
      /******************************************************/
      if (myQmgr  == null)       {myQmgr  = System.getProperty("qm");}
      if (myQueue == null)       {myQueue = System.getProperty("q");}

      /************************************************************/
      /* You could also use a ResourceBundle to get to the        */
      /* amqbdlh.properties file.                                 */
      /************************************************************/
      /* ResourceBundle rb = ResourceBundle.getBundle("amqbdlh"); */
      /* myQmgr  = rb.getString("qm");                            */
      /* myQueue = rb.getString("q");                             */
      /************************************************************/

   }

   /**********************************************************************/
   /* Constructor to read command line arguments (if they were supplied) */
   /**********************************************************************/
   public amqbdlh(String[] args) {

      this(); /* call the default constructor */

      /**********************************/ 
      /* Get the command-line arguments */ 
      /**********************************/ 
      for( int i=0; i<args.length; i++ ) {
          String arg = args[i].toLowerCase();

            if( arg.equals("-qm") ) {
                if ( i+1<args.length ) {
                   myQmgr = args[++i];
                } else {
                   System.out.println("didn't specify qmgr, exiting");
                   return;
                }

            } else if( arg.equals("-q") ) {
                if ( i+1<args.length ) {
                   myQueue = args[++i];
                } else {
                   System.out.println("didn't specify queue, exiting");
                   return;
                }

            } else {
              System.out.println( "Unknown argument: " + arg );
            }
      }

   }
   
   private void prtMessage(MQMessage myMessage) {
	   
	   try {
            System.out.println("****Message descriptor****\n");
            System.out.println("  StrucId  : 'MD  '"    
                             + "  Version : " + myMessage.getVersion()); 
            System.out.println("  Report   : " + myMessage.report
                             + "  MsgType : " + myMessage.messageType);
            System.out.println("  Expiry   : " + myMessage.expiry
                             + "  Feedback : " + myMessage.feedback);
            System.out.println("  Encoding : " + myMessage.encoding 
                             + "  CodedCharSetId : " + myMessage.characterSet);
            System.out.println("  Format : '" + myMessage.format + "'");
            System.out.println("  Priority : " + myMessage.priority 
                             + "  Persistence : " + myMessage.persistence);
            System.out.print("  MsgId : ");
            dumpHexId(myMessage.messageId);
            System.out.print("  CorrelId : ");
            dumpHexId(myMessage.correlationId);
            System.out.println("  BackoutCount : " + myMessage.backoutCount);
            System.out.println("  ReplyToQ     : '" 
                             + myMessage.replyToQueueName + "'");
            System.out.println("  ReplyToQMgr  : '" 
                             + myMessage.replyToQueueManagerName + "'");

            System.out.println("  ** Identity Context");
            System.out.println("  UserIdentifier : '" + myMessage.userId + "'");
            System.out.println("  Accounting Token :");
            System.out.print("   ");
            dumpHexId(myMessage.accountingToken);
            System.out.println("  ApplIdentityData : '" 
                                + myMessage.applicationIdData + "'");
        
            System.out.println("  ** Origin Context");
            System.out.println("  PutApplType    : '" 
                               + myMessage.putApplicationType + "'");
            System.out.println("  PutApplName    : '" 
                               + myMessage.putApplicationName + "'");

            System.out.print("  PutDate  : '");
            System.out.print(myMessage.putDateTime.get(Calendar.YEAR));
            int myMonth = myMessage.putDateTime.get(Calendar.MONTH) + 1;
            if (myMonth < 10) {System.out.print("0");}
            System.out.print(myMonth);

            int myDay = myMessage.putDateTime.get(Calendar.DAY_OF_MONTH);
            if (myDay < 10) {System.out.print("0");}
            System.out.print(myDay);
            System.out.print("'    ");

            System.out.print("PutTime  : '");
            int myHour = myMessage.putDateTime.get(Calendar.HOUR_OF_DAY);
            if (myHour < 10) { System.out.print("0"); }
            System.out.print(myHour);

            int myMinute = myMessage.putDateTime.get(Calendar.MINUTE);
            if (myMinute < 10) { System.out.print("0"); }
            System.out.print(myMinute);

            int mySecond = myMessage.putDateTime.get(Calendar.SECOND);
            if (mySecond < 10) { System.out.print("0"); }
            System.out.print(mySecond);

            int myMsec = myMessage.putDateTime.get(Calendar.MILLISECOND);
            myMsec = myMsec/10;
            if (myMsec < 10) { System.out.print("0"); }
            System.out.print(myMsec); 
            System.out.println("'");

            System.out.println("  ApplOriginData : '" 
                               + myMessage.applicationOriginData + "'");
            System.out.println();
            System.out.print("  GroupId : ");
            dumpHexId(myMessage.groupId);
            System.out.println("  MsgSeqNumber   : '" 
                               + myMessage.messageSequenceNumber + "'");
            System.out.println("  Offset         : '" + myMessage.offset + "'");
            System.out.println("  MsgFlags       : '" 
                               + myMessage.messageFlags + "'");
            System.out.println("  OriginalLength : '" 
                               + myMessage.originalLength + "'");
            System.out.println();

            System.out.println("****   Message     ****");
            System.out.println();
            System.out.println(" length - " + myMessage.getMessageLength() 
                             + " bytes\n");
            dumpHexMessage(myMessage);
            System.out.println();
            System.out.println();
      } catch (java.io.IOException ex) {
         System.out.println("An IO error occurred: " + ex);
      }
   }
   
   private void handleException(MQException ex) {
   }
   
   private void handleException(MQDataException dex) {
   }

   /********************************************************/
   /* This method does the actually browsing of the queue. */
   /********************************************************/
   public void myBrowser() {
      MQQueueManager qMgr        = null;
      MQQueue        browseQueue = null;
	  MQDLH dlh = new MQDLH ();
      int j = 0; /* used as a message counter */
      
      // System.out.println("\namqbdlh.java - starts here");
      // System.out.println("**************************");
      MQException.log = null; /* don't print out any exceptions */
      try {
         /*****************************/
         /* Create a queue manager    */
         /*****************************/
         qMgr = new MQQueueManager(myQmgr);

         /****************************************/
         /* Open the queue in browse mode.       */
         /****************************************/
         int openOptions = MQC.MQOO_BROWSE;
		 int myrc = 0;
         browseQueue = qMgr.accessQueue(myQueue, openOptions, null, null, null);
         // System.out.println("\n OPEN - '" + myQueue + "'\n\n");

         /*****************************************************************/
         /* Loop through the queue browsing the messages that are on it.  */
         /*****************************************************************/
         MQGetMessageOptions gmo = new MQGetMessageOptions(); 
         gmo.options = gmo.options + MQC.MQGMO_BROWSE_NEXT + MQC.MQGMO_ACCEPT_TRUNCATED_MSG;
         MQMessage myMessage = new MQMessage();
		 myMessage.resizeBuffer(200);
		 
         while (myrc != MQException.MQRC_NO_MSG_AVAILABLE) {
			 
            myMessage.clearMessage();
            myMessage.correlationId = MQC.MQCI_NONE;
            myMessage.messageId     = MQC.MQMI_NONE;
			try {
              browseQueue.get(myMessage, gmo);
			} catch (MQException getex) {
				myrc = getex.reasonCode;
				if (getex.reasonCode == MQException.MQRC_NO_MSG_AVAILABLE) {
					// System.out.println(" No more messages");
				} else {
					if (getex.reasonCode != MQException.MQRC_TRUNCATED_MSG_ACCEPTED) {
						System.out.println("MQ error: Completion code " +
								getex.completionCode + " Reason code " + getex.reasonCode);
					}
				}
			};
            
            j = j + 1;
			try {
				dlh.read(myMessage);
				System.out.println(dlh.getReason() + "," + dlh.getDestQName() + "," + dlh.getDestQMgrName() + "," + dlh.getPutApplName());
			} catch(MQDataException dex) {
				// System.out.println("MQDataException: " + dlh.getReason());
			};
			/* */
            /* System.out.println(" GET of message number " + j); */
			/* prtMessage(myMessage); */
			/* */


         }

      } catch (MQException ex) {
         if (ex.reasonCode == MQException.MQRC_NO_MSG_AVAILABLE) {
            // System.out.println(" No more messages");
         } else {
            // System.out.println("MQ error: Completion code " +
                      // ex.completionCode + " Reason code " + ex.reasonCode);
         }
      } catch (java.io.IOException ex) {
         System.out.println("An IO error occurred: " + ex);
      }
        
      try {
         browseQueue.close();
         // System.out.println(" CLOSE of queue");
         qMgr.disconnect();
         // System.out.println(" DISCONNECT from queue manager");
      } catch (MQException ex) {
         System.out.println("MQ error: Completion code " +
                      ex.completionCode + " Reason code " + ex.reasonCode);
      }

      // System.out.println("**************************");
      // System.out.println("amqbdlh.java finished");

   } 

   /*************************************************************/
   /* This method will dump the text of the message in both hex */
   /* and character format.                                     */
   /*************************************************************/
   static void dumpHexMessage(MQMessage myMsg) throws java.io.IOException {
       
      int DataLength = myMsg.getMessageLength();
      int ch      = 0;
      int chars_this_line = 0;
      int CHARS_PER_LINE  = 16;
      StringBuffer line_text = new StringBuffer();
      line_text.setLength(0);
      do {
         chars_this_line = 0;
         String myPos = new String(Integer.toHexString(ch));
         for (int i=0; i < 8 - myPos.length(); i++) {
            System.out.print("0");
         }
         System.out.print((String)(Integer.toHexString(ch)).toUpperCase() + ": ");

         while ( (chars_this_line < CHARS_PER_LINE) &&
                 (ch < DataLength) ) {

             if (chars_this_line % 2 == 0) {
                System.out.print(" ");
             }
             char b = (char)(myMsg.readUnsignedByte() & 0xFF);
             if (b < 0x10) {
                System.out.print("0");
             }

             System.out.print((String)(Integer.toHexString(b)).toUpperCase());

             /***********************************************/ 
             /* If this is a printable character, print it. */
             /* Otherwise, print a '.' as a placeholder.    */
             /***********************************************/ 
             if (Character.isLetterOrDigit(b)
                 || Character.getType(b) == Character.CONNECTOR_PUNCTUATION
                 || Character.getType(b) == Character.CURRENCY_SYMBOL
                 || Character.getType(b) == Character.MATH_SYMBOL
                 || Character.getType(b) == Character.MODIFIER_SYMBOL
                 || Character.getType(b) == Character.UPPERCASE_LETTER
                 || Character.getType(b) == Character.SPACE_SEPARATOR
                 || Character.getType(b) == Character.DASH_PUNCTUATION
                 || Character.getType(b) == Character.START_PUNCTUATION
                 || Character.getType(b) == Character.END_PUNCTUATION
                 || Character.getType(b) == Character.OTHER_PUNCTUATION ) {
                line_text.append(b); 
             } else {
                line_text.append('.');
             }
             chars_this_line++;
             ch++;
         }

         /*****************************************************/
         /* pad with blanks to format the last line correctly */
         /*****************************************************/
         if (chars_this_line < CHARS_PER_LINE) {
           for ( ;chars_this_line < CHARS_PER_LINE; chars_this_line++) {
              if (chars_this_line % 2 == 0) System.out.print(" ");
              System.out.print("  ");
              line_text.append(' ');
           }
         }

         System.out.println(" '" + line_text.toString() + "'");
         line_text.setLength(0);
       } while (ch < DataLength);

   } /* end of dumpHexMessage */

   /****************************************************/
   /* Some of the MQ Ids are actually byte strings and */
   /* need to be dumped in hex format.                 */
   /****************************************************/
   static void dumpHexId(byte[] myId) {
      System.out.print("X'");
      for (int i=0; i < myId.length; i++) {
        char b = (char)(myId[i] & 0xFF);
        if (b < 0x10) {
           System.out.print("0");
        }
        System.out.print((String)(Integer.toHexString(b)).toUpperCase());
      }
      System.out.println("'");
   }


    /****************************/
    /* main program entry point */
    /****************************/
    public static void main( String[] args )     {
       amqbdlh app = new amqbdlh(args);
       
       /******************************************/ 
       /* Check that all arguments were entered. */ 
       /******************************************/ 
       if (     (myQmgr==null)  
             || (myQueue==null) ) {
          System.out.println("Usage:");
          System.out.println("java amqbdlh -qm ... -q ...");
          System.out.println("where -qm is the queue manager name");
          System.out.println("      -q  is the queue name");
       } else {
          app.myBrowser();
       }
    }

   
}


