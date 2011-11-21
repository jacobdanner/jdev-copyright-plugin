package com.ph477y.jdev.copyright;

import oracle.ide.Context;
import oracle.ide.controller.ContextMenu;
import oracle.ide.controller.ContextMenuListener;
import oracle.ide.controller.IdeAction;
import oracle.ide.model.Element;
import oracle.ide.model.TextNode;

/**
 * @author jacob.danner@gmail.com
 */
public class InsertCopyrightContextListener
  implements ContextMenuListener
{
  public InsertCopyrightContextListener()
  {
    super();
  }

  public void menuWillShow(ContextMenu contextMenu)
  {
    Context context = contextMenu.getContext();

    if(context.getNode() instanceof TextNode)
    {
      addInsertMenu(contextMenu);
    } else {
      Element[] elements = context.getSelection();
      for (Element elem: elements)
      {
        if (elem instanceof TextNode)
        {
          addInsertMenu(contextMenu);
          break;
        }
      }
    }
  }


  private void addInsertMenu(ContextMenu contextMenu)
  {
    IdeAction action = IdeAction.find(InsertCopyrightActionController.INSERT_COPYRIGHT_CMD);
    contextMenu.add(contextMenu.createMenuItem(action));
  }

  public void menuWillHide(ContextMenu contextMenu)
  {
  }

  public boolean handleDefaultAction(Context context)
  {
    return false;
  }
}
