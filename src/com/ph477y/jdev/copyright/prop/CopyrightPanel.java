package com.ph477y.jdev.copyright.prop;

import oracle.ide.config.Preferences;
import oracle.ide.extension.RegisteredByExtension;
import oracle.ide.panels.ApplyEvent;
import oracle.ide.panels.ApplyListener;
import oracle.ide.panels.DefaultTraversablePanel;
import oracle.ide.panels.TraversableContext;
import oracle.ide.util.ResourceUtils;
import oracle.ide.webbrowser.URLHyperlinkButton;
import oracle.javatools.controls.HyperlinkButton;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;

/**
 * Copyright preference page implementation.
 */
@RegisteredByExtension("com.ph477y.jdev")
final class CopyrightPanel
  extends DefaultTraversablePanel implements ApplyListener
{
  private JLabel copyrightLbl = new JLabel("Copyright:");
  private JTextArea copyrightTxt = new JTextArea();
  private URLHyperlinkButton findLicenseBtn = new URLHyperlinkButton();

  public CopyrightPanel()
  {
    try
    {
      jbInit();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
      throws Exception
  {
    setLayout(new GridBagLayout());
    JScrollPane licenseScrollPane = new JScrollPane(copyrightTxt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    licenseScrollPane.getViewport().add(copyrightTxt, null);

    ResourceUtils.resLabel(copyrightLbl, licenseScrollPane, "Copyright:");
    findLicenseBtn.setAlwaysUnderlined(true);
    findLicenseBtn.setURL(new URL("http://www.opensource.org/licenses/category"));
    ResourceUtils.resButton(findLicenseBtn, "Find a License...");


    GridBagConstraints labelGbc = new GridBagConstraints();
    labelGbc.fill = GridBagConstraints.BOTH;
    labelGbc.weightx = 0.9;
    labelGbc.gridx = 0;
    labelGbc.gridy = 0;
    labelGbc.gridwidth = GridBagConstraints.REMAINDER;
    add(copyrightLbl, labelGbc);

    GridBagConstraints scrollPanelGbc = new GridBagConstraints();
    scrollPanelGbc.fill = GridBagConstraints.BOTH;
    scrollPanelGbc.weightx = 0.9;
    scrollPanelGbc.weighty = 0.9;
    scrollPanelGbc.gridx = 0;
    scrollPanelGbc.gridy = 1;
    scrollPanelGbc.gridwidth = GridBagConstraints.REMAINDER;
    scrollPanelGbc.gridheight = GridBagConstraints.REMAINDER;

    add(licenseScrollPane, scrollPanelGbc);

    GridBagConstraints licenseGbc = new GridBagConstraints();
    licenseGbc.fill = GridBagConstraints.BOTH;
    licenseGbc.weightx = 0.9;
    licenseGbc.gridx = 0;
    licenseGbc.gridy = 2;
    licenseGbc.gridwidth = GridBagConstraints.REMAINDER;
    add(findLicenseBtn, BorderLayout.PAGE_END);
  }

  /**
   * Fetch the EditorOptions from the traversable  context.
   *
   * @param tc the traversable context
   * @return the EditorOptions instance containing the current options
   */
  private CopyrightInfo findCopyrightInfo(TraversableContext tc)
  {
    return CopyrightInfo.getInstance(tc.getPropertyStorage());
  }

  public void onEntry(TraversableContext context)
  {
    final CopyrightInfo info = CopyrightInfo.getInstance(Preferences.getPreferences());
    copyrightTxt.setText(info.getCopyright());

  }

  public void onExit(TraversableContext context)
  {
    final String copyrightValue = copyrightTxt.getText();
    final CopyrightInfo info = CopyrightInfo.getInstance(Preferences.getPreferences());
    info.setCopyright(copyrightValue);
  }

  public void apply(ApplyEvent event)
  {
    final TraversableContext tc = (TraversableContext) event.getSource();
    final CopyrightInfo options = findCopyrightInfo(tc);

    // Apply the options globally
    //applyOptions(options);
  }

  public void cancel(ApplyEvent applyEvent)
  {
    // NO-OP
  }
}
