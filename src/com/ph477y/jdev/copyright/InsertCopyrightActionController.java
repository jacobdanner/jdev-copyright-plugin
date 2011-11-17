package com.ph477y.jdev.copyright;

import oracle.bali.xml.addin.XMLSourceNode;
import oracle.ide.Context;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.extension.RegisteredByExtension;

import oracle.ide.model.Element;
import oracle.ide.model.TextNode;
import oracle.ide.net.URLFileSystem;
import oracle.jdeveloper.model.JavaSourceNode;

import java.util.ArrayList;


/**
 * Controller for action insertcopyrightaction_id.
 */
@RegisteredByExtension("com.ph477y.jdev")
public final class InsertCopyrightActionController
    implements Controller
{       // implement BasicWriteAction
  // see CodeEditorController
  private ArrayList<String> XML_FILETYPES = new ArrayList<String>();
  public boolean update(IdeAction action, Context context)
  {
    // TODO Determine when insertcopyrightaction_id is enabled, and call action.setEnabled().
    //assert action.getCommandId() == ToolsIds.GENERATE_EXTENSION_DEPS_CMD;
    // TODO: add a check to see if we have text nodes
    action.setEnabled(context.getSelection() != null);
    return true;
  }

  public boolean handleEvent(IdeAction action, Context context)
  {
    Element[] elements = context.getSelection();

    for (Element elem : elements)
    {
      if (elem instanceof TextNode)
      {
        TextNode node = (TextNode) elem;
        /*  BasicEditorKit.ToggleCommentsAction
         // Before we start the edit, fetch the document's single-line
      // comment starter (if any)
      LanguageSupport support = document.getLanguageSupport();
      String commentDelimiter = (String)
        support.getProperty( LanguageSupport.PROPERTY_LINE_COMMENT_START );
      if ( ( commentDelimiter == null ) ||
           ( commentDelimiter.length() == 0 ) )
      {
        return;
      }
         */
        final String ext = URLFileSystem.getSuffix(node.getURL());
        // TODO look in languageModule for supported info
        if(XML_FILETYPES.contains(ext))
        {
          // Handle XML files
          // check for <? line then add
          //<!-- license txt -->

        } else if (JAVA_FILETYPE.contains(ext))
        {
          // \/\*\*
          // \*\/
        } else if (JSP_FILETYPE.contains(ext))
        {
          // <%-- comment --%>

        } else {
          // add
          // # Unknown File Type - please correct commenting of license text
          // <LICENSE_TXT>
        }




      }
      else
      {
        // cannot handle type elem.class.getName();
      }
    }

    return false;
  }
}
