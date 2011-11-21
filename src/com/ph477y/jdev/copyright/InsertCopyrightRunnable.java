/*
 * Copyright (c) 2011, Jacob Danner - jacob.danner@gmail.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.opensource.org/licenses/BSD-2-Clause
 */

package com.ph477y.jdev.copyright;

import oracle.ide.Context;
import oracle.ide.config.FileTypesRecognizer;
import oracle.ide.controller.Command;
import oracle.ide.dialogs.ProgressBar;
import oracle.ide.externaltools.ExternalToolManager;
import oracle.ide.externaltools.macro.MacroRegistry;
import oracle.ide.model.Node;
import oracle.ide.model.TextNode;
import oracle.ide.net.URLFileSystem;
import oracle.ide.net.URLKey;
import oracle.ide.vcs.VCSManager;
import oracle.javatools.buffer.TextBuffer;
import oracle.javatools.editor.language.LanguageModule;
import oracle.jdeveloper.audit.transform.TextBufferCommand;
import oracle.jdeveloper.vcs.spi.VCSCheckOutNodeCmd;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Bulk of the work done in this class
 *
 * @author jacob.danner@gmail.com
 */
public class InsertCopyrightRunnable extends VCSCheckOutNodeCmd implements Runnable
{
  private List<TextNode> nodes;
  private String copyrightTxt;
  private Context context;

  private ProgressBar progressBar;
  private MacroRegistry macroRegistry;
  private List<String> slashStarList;
  private List<String> jspExtList;
  private List<String> rbExtList;
  private List<String> hashExtList;
  //private List<String> colonExtList;
  private List<String> winExtList;

  public InsertCopyrightRunnable(List<TextNode> nodes, String copyrightTxt, Context context)
  {
    super(Command.NO_UNDO, "Updating Command Command");
    this.nodes = nodes;
    this.copyrightTxt = copyrightTxt;
    this.context = context;
    final ExternalToolManager extToolManager = ExternalToolManager.getExternalToolManager();
    macroRegistry = extToolManager.getMacroRegistry();
    initExtensions();
  }

  /**
   * I really wish there were a more programmatic way to do this
   */
  private void initExtensions()
  {
    String[] slashStarExt = new String[]{LanguageModule.FILETYPE_C,
      LanguageModule.FILETYPE_CPLUSPLUS,
      LanguageModule.FILETYPE_CPP,
      LanguageModule.FILETYPE_CPP2,
      LanguageModule.FILETYPE_H,
      LanguageModule.FILETYPE_JAVA,
      LanguageModule.FILETYPE_JS,
      LanguageModule.FILETYPE_CSS,
      LanguageModule.FILETYPE_PHP,
      LanguageModule.FILETYPE_PHP3,
      LanguageModule.FILETYPE_PHP4,
      LanguageModule.FILETYPE_PLSQL,
      LanguageModule.FILETYPE_SQL,
      LanguageModule.FILETYPE_SQLJ,
      LanguageModule.FILETYPE_IDL,
      "ml",
      "cs"};
    slashStarList = Arrays.asList(slashStarExt);

    //<%-- -->
    // JSPSourceNode
    // TODO: is jspx valid here
    String[] jspExt = new String[]{LanguageModule.FILETYPE_JSP};//, "jspx"};
    jspExtList = Arrays.asList(jspExt);

    // rb can have multiple line using =begin/=end
    String[] rbExt = new String[]{"rb", "erb", "rhtml"};
    rbExtList = Arrays.asList(rbExt);

    String[] singleHashExt = new String[]{LanguageModule.FILETYPE_TXT,
      LanguageModule.FILETYPE_TEXT,
      LanguageModule.FILETYPE_RTS,
      LanguageModule.FILETYPE_PROPERTIES,
      LanguageModule.FILETYPE_LOG,
      "py", "sh", "properties"};
    hashExtList = Arrays.asList(singleHashExt);

    // : <- IDL File
    //String[] singleLineColonExt = new String[]{LanguageModule.FILETYPE_IDL};
    //colonExtList = Arrays.asList(singleLineColonExt);

    // REM <- BATCH File
    String[] windowsShellExt = new String[]{"bat", "cmd"};
    winExtList = Arrays.asList(windowsShellExt);
  }


  /**
   * this needs to be called after initialization, which seems quite odd
   * This is how its done in the ESDK sample - ProgressBar
   *
   * @param progressBar
   */
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
    assert progressBar != null;
    try
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
              String suffix = URLFileSystem.getSuffix(nodeUrl.toURL());
              if (!suffix.isEmpty() && FileTypesRecognizer.isXmlExtension(suffix))
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
      }

    } finally
    {
      progressBar.setDoneStatus();
    }
  }

  private String getCopyrightForInsert(URL nodeUrl, TextNode node)
  {
    // get the extension, handling special cases like
    // README, etc.
    String ext = URLFileSystem.getSuffix(nodeUrl);

    String className = "";
    boolean isXmlNode = false;
    if(!ext.isEmpty())
    {
      className = FileTypesRecognizer.getClassNameForExtension(ext);
      isXmlNode = FileTypesRecognizer.isXmlExtension(ext);
    }

    // sanitize to remove leading .
    // because thats how LanguageModule.FILETYPE_*
    // returns things
    if (ext.startsWith(".") && ext.length() > 1)
    {
      ext = ext.substring(1, ext.length());
    }

    boolean isJavaNode = className.endsWith("JavaSourceNode");

    StringBuffer builder = new StringBuffer();

    StringBuffer cpTxt = new StringBuffer(copyrightTxt);
    if (context != null)
    {
      Context currContext = context;
      currContext.setNode(node);
      String moddedText = macroRegistry.expand(copyrightTxt, currContext);
      cpTxt = new StringBuffer(moddedText);
    }

    if (isXmlNode)
    {
      builder.append("<!--\n");
      builder.append(cpTxt);
      builder.append("-->\n");
    }
    else if (isJavaNode || slashStarList.contains(ext))
    {
      builder.append("/*\n");
      builder.append(prependCopy(cpTxt.toString(), "*"));
      builder.append("*/");
    }
    else if (jspExtList.contains(ext))
    {
      builder.append("<%--\n");
      builder.append(prependCopy(cpTxt.toString(), "-"));
      builder.append("-->");

    }
    else if (rbExtList.contains(ext))
    {
      builder.append("=begin\n");
      builder.append(prependCopy(cpTxt.toString(), "#"));
      builder.append("=end");
    }
    else if (hashExtList.contains(ext))
    {
      builder.append(prependCopy(cpTxt.toString(), "#"));
    }
    //else if (colonExtList.contains(ext))
    //{
    //  builder.append(prependCopy(cpTxt.toString(), ";"));
    //}
    else if (winExtList.contains(ext))
    {
      builder.append(prependCopy(cpTxt.toString(), "REM"));
    }
    else
    {
      // by default, just write the copyright file no, additional modifications
      builder.append(cpTxt.toString());
    }
    return builder.toString();
  }


  private String prependCopy(String copyright, String prependChar)
  {
    // TODO: should we be using
    // TextBuffer.EOL_* values here instead?
    final String[] cpLines = copyright.toString().split("\n");
    StringBuilder copyTxt = new StringBuilder();
    for (String line : cpLines)
    {
      copyTxt.append(prependChar);
      copyTxt.append(" ");
      copyTxt.append(line);
      if(!line.endsWith("\n"))
      {
        copyTxt.append("\n");
      }
    }
    copyTxt.append(prependChar);
    copyTxt.append("\n");
    return copyTxt.toString();
  }
}
