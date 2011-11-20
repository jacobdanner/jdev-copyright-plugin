package com.ph477y.jdev.copyright;

import com.ph477y.jdev.copyright.prop.CopyrightInfo;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.config.FileTypesRecognizer;
import oracle.ide.config.Preferences;
import oracle.ide.controller.Command;
import oracle.ide.dialogs.ProgressBar;
import oracle.ide.model.Node;

import oracle.ide.model.TextNode;
import oracle.ide.net.URLFileSystem;
import oracle.ide.net.URLKey;
import oracle.ide.vcs.VCSManager;
import oracle.javatools.buffer.TextBuffer;
import oracle.javatools.util.Pair;
import oracle.jdeveloper.vcs.spi.VCSCheckOutNodeCmd;
import oracle.jdeveloper.audit.transform.TextBufferCommand;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class AddCopyrightCommand extends Command
{
  private final static Logger LOG = Logger.getLogger(AddCopyrightCommand.class.getName());
  final static String INSERT_CMD = "com.ph477y.jdev.copyright.InsertCopyrightCommand";
  final static int INSERT_CMD_ID = Ide.findOrCreateCmdID(INSERT_CMD);

  private final List<TextNode> nodes;
  private ProgressBar progressBar;
  private InsertCopyrightRunnable copyrightRunnable;

  private final String copyrightTxt;
  //private final FileTypesPrefs ftp;
  private FileTypesRecognizer ftp = new FileTypesRecognizer();

  public AddCopyrightCommand(List<TextNode> nodes, Context context)
  {
    super(INSERT_CMD_ID, Command.NO_UNDO, INSERT_CMD);
    this.nodes = nodes;
    //Preferences preferences = Preferences.getPreferences();
    //ftp = FileTypesPrefs.getInstance(preferences);

    CopyrightInfo copyrightInfo = CopyrightInfo.getInstance(Preferences.getPreferences());
    copyrightTxt = copyrightInfo.getCopyright();

    copyrightRunnable = new InsertCopyrightRunnable(this.nodes, copyrightTxt, context);
    progressBar = new ProgressBar(Ide.getMainWindow(),
      "Inserting Copyright", copyrightRunnable, false);//true);
    // this seems quite odd, but is required via
    // ProgressBar ESDK
    copyrightRunnable.setProgressBar(progressBar);

  }

  @Override
  public int doit() throws Exception
  {
    progressBar.setCancelable(true);
    progressBar.setDialogName("Inserting Copyright");
    progressBar.start("Checking File", "Updating File", 0, nodes.size());
    progressBar.waitUntilDone();
    /*while(!progressBar.hasUserCancelled())
    {
      //copyrightRunnable
      progressBar.setDoneStatus();
    }*/
    return 0;
  }

  public class InsertCopyrightRunnable extends VCSCheckOutNodeCmd implements Runnable
  {
    private List<TextNode> nodes;
    private String copyrightTxt;
    private Context context;

    private ProgressBar progressBar;

    public InsertCopyrightRunnable(List<TextNode> nodes, String copyrightTxt, Context context)
    {
      super(Command.NO_UNDO, "Updating Command Command");
      this.nodes = nodes;
      this.copyrightTxt = copyrightTxt;
      this.context = context;
    }

    public void setProgressBar(ProgressBar progressBar)
    {
      this.progressBar = progressBar;
    }


    public void logMessage(Integer step, Node node, String msg)
    {
      if (progressBar.hasUserCancelled() || progressBar.getCompletionStatus() == nodes.size())
      {
        progressBar.updateProgress(step, node.getShortLabel(), msg);
      }
    }

    public void run()
    {
      final int nodeListSize = nodes.size();
      for (int i = 0; i < nodeListSize; i++)
      {
        if (!progressBar.hasUserCancelled())
        {
          TextNode node = nodes.get(i);
          final URLKey nodeUrl = URLKey.getInstance(node.getURL());
          boolean canCheckout = VCSManager.getVCSManager().canCheckOut(nodeUrl.toURL());//true;//;//canCheckOutUI(nodeUrl.toURL());
          boolean doesCheckout = true; // assume true so we can process the file, unless doCheckout returns false

          // Need to figure out the proper way to call these APIs, canCheckoutUI always prompts
          // maybe we should try
          // oracle.jdeveloper.refactoring.util.MakeWritableHelper
          logMessage(i, node, "Checking VCS of " + node.getShortLabel());
          if (canCheckout)
          {
            try
            {
              doesCheckout = doCheckOut(nodeUrl.toURL());
            } catch (Exception e)
            {
              doesCheckout = false;
              e.printStackTrace();
            }
          }
          if (doesCheckout)
          {
            logMessage(i, node, "VCS Check complete " + doesCheckout + " " + node.getShortLabel());
            final String toinsertTxt = getCopyrightForInsert(nodeUrl.toURL(), node);
            try
            {
              int offset = 0; // for most cases, 0 is fine, but if XML starts with <? ... we should go to the next line
              if (FileTypesRecognizer.isXmlExtension(URLFileSystem.getSuffix(nodeUrl.toURL())))
              {
                TextBuffer tb = node.tryAcquireTextBuffer();
                tb.readLock();
                String firstLine = tb.getString(0, 5);
                // <?xml
                if (firstLine.compareToIgnoreCase("<?xml") == 0)
                {
                  offset = tb.getLineMap().getLineStartOffset(1);
                }
                tb.readUnlock();
              }
              TextBufferCommand insertCommand = new TextBufferCommand("Inserting Copyright", node.acquireTextBufferOrThrow());
              insertCommand.insert(offset, toinsertTxt);
              insertCommand.doit();
            } catch (Exception ioEx)
            {
              logMessage(i, node, "Failed to add copyright");
            }
          }
        }
        else
        {
          progressBar.setDoneStatus();
          return;
        }
      }
      progressBar.setDoneStatus();
      return;
    }

    private String getCopyrightForInsert(URL nodeUrl, TextNode node)
    {

      final String ext = URLFileSystem.getSuffix(nodeUrl);
      String className = FileTypesRecognizer.getClassNameForExtension(ext);
      LOG.warning("NODETYPE - " + className);
      //ContentType contentType = FileTypesRecognizer.getContentTypeForExtension(ext);
      //LOG.warning("CONTENTTYPE - " + contentType);

      //MetaClass<Node> nodeType = FileTypesRecognizer.recognizeURLAsMeta(nodeUrl);
      StringBuilder builder = new StringBuilder();
      Pair<String, String> multiLineComment = new Pair<String, String>();
      if (FileTypesRecognizer.isXmlExtension(ext))
      {
        multiLineComment.setFirst("<!--\n");
        multiLineComment.setSecond("\n-->\n");
      }
      else
      {
        multiLineComment.setFirst("/**\n");
        multiLineComment.setSecond("\n*/\n");
      }
      builder.append(multiLineComment.getFirst());
      if(context != null)
      {
        Context currContext = context;
        currContext.setNode(node);
        // String moddedText = new MacroRegistry().expand(copyrightTxt, currContext);
        builder.append(copyrightTxt);
      } else {
        builder.append(copyrightTxt);
      }
        builder.append(multiLineComment.getSecond());
      return builder.toString();
    }

  }

}