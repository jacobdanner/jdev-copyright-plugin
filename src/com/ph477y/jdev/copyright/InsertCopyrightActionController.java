package com.ph477y.jdev.copyright;

//import oracle.bali.xml.addin.XMLSourceNode;
import oracle.ide.Context;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.extension.RegisteredByExtension;

import oracle.ide.model.Element;
import oracle.ide.model.Node;
import oracle.ide.model.TextNode;
import oracle.ide.net.URLFileSystem;
//import oracle.jdeveloper.model.JavaSourceNode;

import java.util.ArrayList;

import java.util.List;
import java.util.logging.Logger;

import oracle.ide.Ide;
import oracle.ide.controller.TriggerController;

import oracle.javatools.editor.language.LanguageSupport;


/**
 * Controller for action com.ph477y.jdev.InsertCopyrightActionId
 */
@RegisteredByExtension("com.ph477y.jdev")
public final class InsertCopyrightActionController
  implements TriggerController
{
  public static final String INSERT_COPYRIGHT_CMD = "com.ph477y.jdev.InsertCopyrightActionId";
  //public static final IdeAction INSERT_COPYRIGHT_CMD_IA = IdeAction.find(INSERT_COPYRIGHT_CMD);
  public static final int INSERT_COPYRIGHT_CMD_ID = Ide.findCmdID(INSERT_COPYRIGHT_CMD);
  private final Logger LOG = Logger.getLogger(InsertCopyrightActionController.class.getName());

  //private ArrayList<String> XML_FILETYPES = new ArrayList<String>();

  public boolean update(IdeAction action, Context context)
  {
    if (action.getCommandId() == INSERT_COPYRIGHT_CMD_ID)
    {
      // TODO: add a check to see if we have text nodes
      action.setEnabled(context.getSelection() != null);
      return true;
    }
    return false;
  }

  public boolean handleEvent(IdeAction action, Context context)
  {
    if (action.getCommandId() == INSERT_COPYRIGHT_CMD_ID)
    {
      Element[] elements = context.getSelection();

      List<TextNode> nodes = new ArrayList<TextNode>();
      for (Element elem: elements)
      {
        if (elem instanceof TextNode)
        {
          TextNode node = (TextNode) elem;
          // it looks like the best way to handle this is to
          // create multiline blocks based on filetypes via suffix/extensions
          final String ext = URLFileSystem.getSuffix(node.getURL());
          LOG.info(INSERT_COPYRIGHT_CMD + " - " + ext);
          nodes.add(node);
          // TODO: Call command here
          // command needs to
          // figure out proper multiline comments
          // acquire lock
          // check out VCS
          // modify
          // end lock
        }
      }
      AddCopyrightCommand addCmd = new AddCopyrightCommand(nodes);
      try
      {
        addCmd.doit();
      } catch (Exception e)
      {
        e.printStackTrace();
        return false;
      }
      return true;
    }
    return false;
  }

  public Object getInvalidStateMessage(IdeAction ideAction, Context context)
  {
    return null;
  }
}
// SCRATCHPAD FOR LATER
  /*    //  BasicEditorKit.ToggleCommentsAction
      // Before we start the edit, fetch the document's single-line
      // comment starter (if any)
      /*LanguageSupport support = document.getLanguageSupport();
      String commentDelimiter = (String)
        support.getProperty( LanguageSupport.PROPERTY_LINE_COMMENT_START );
      if ( ( commentDelimiter == null ) ||
           ( commentDelimiter.length() == 0 ) )
      {
        return;
      }
        // TODO look in languageModule for supported info
        if(XML_FILETYPES.contains(ext))
        {
          // Handle XML files
          // check for <? line then add
          //<!-- license txt -->
        //} else if (JAVA_FILETYPE.contains(ext))
        //{
          // \/\*\*
          // \*\/
        //} else if (JSP_FILETYPE.contains(ext))
        //{
          // <%-- comment --%>
        //} else {
          // add
          // # Unknown File Type - please correct commenting of license text
          // <LICENSE_TXT>
        }
      //}
      //else
      //{
        // cannot handle type elem.class.getName();
      //}
    //}
    return false;
  */