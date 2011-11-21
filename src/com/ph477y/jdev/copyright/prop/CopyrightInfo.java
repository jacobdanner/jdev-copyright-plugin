package com.ph477y.jdev.copyright.prop;

import oracle.ide.extension.RegisteredByExtension;

import oracle.javatools.data.HashStructure;
import oracle.javatools.data.HashStructureAdapter;
import oracle.javatools.data.PropertyStorage;

@RegisteredByExtension("com.ph477y.jdev.copyright")
public final class CopyrightInfo
  extends HashStructureAdapter
{
  public static final String DATA_KEY = CopyrightInfo.class.getName();
  final static String COPYRIGHT_TXT = ".copyrightTxt"; // NOTRANS
  private final static String DEFAULT_LICENSE_TXT = "Enter your copyright here";

  private CopyrightInfo(HashStructure hash)
  {
    super(hash);
  }

  /**
   * Returns an instance of <tt>CopyrightInfo</tt>
   *
   * @param storage a storage object.
   * @throws NullPointerException if <tt>storage</tt> is <tt>null</tt>.
   */
  public static CopyrightInfo getInstance(PropertyStorage storage)
  {
    return new CopyrightInfo(findOrCreate(storage, DATA_KEY));
  }

  public String getCopyright()
  {
    return _hash.getString(COPYRIGHT_TXT, DEFAULT_LICENSE_TXT);
  }

  public void setCopyright(String txt)
  {
    _hash.putString(COPYRIGHT_TXT, txt.trim());
  }

}
