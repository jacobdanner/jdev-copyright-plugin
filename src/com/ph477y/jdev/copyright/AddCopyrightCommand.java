package com.ph477y.jdev.copyright;

import com.ph477y.jdev.copyright.prop.CopyrightInfo;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.config.FileTypesRecognizer;
import oracle.ide.config.Preferences;
import oracle.ide.controller.Command;
import oracle.ide.dialogs.ProgressBar;

import oracle.ide.model.TextNode;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command to handle updating files
 * @author jacob.danner@gmail.com
 */
public class AddCopyrightCommand extends Command
{
  private final static Logger LOG = Logger.getLogger(AddCopyrightCommand.class.getName());
  final static String INSERT_CMD = "com.ph477y.jdev.copyright.InsertCopyrightCommand";
  final static int INSERT_CMD_ID = Ide.findOrCreateCmdID(INSERT_CMD);

  private final List<TextNode> nodes;
  private ProgressBar progressBar;
  private InsertCopyrightRunnable copyrightRunnable;

  private final String copyrightTxt;
  private FileTypesRecognizer ftp = new FileTypesRecognizer();

  public AddCopyrightCommand(List<TextNode> nodes, Context context)
  {
    super(INSERT_CMD_ID, Command.NO_UNDO, INSERT_CMD);
    this.nodes = nodes;
    CopyrightInfo copyrightInfo = CopyrightInfo.getInstance(Preferences.getPreferences());
    copyrightTxt = copyrightInfo.getCopyright();

    copyrightRunnable = new InsertCopyrightRunnable(this.nodes, copyrightTxt, context);
    // with some simple checks for context and Ide.getMainwindow, we could make this command
    // executable in headless mode
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

}