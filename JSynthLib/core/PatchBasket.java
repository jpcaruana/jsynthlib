package core;
import javax.swing.JInternalFrame;
import java.awt.*;
import java.io.*;
import java.util.*;

/**This interface should be implemented by any window which serves as a
   holder or "basket" for patches. It is implemented by LibraryFrame, the
   Library window and also BankEditorFrame*/
public interface PatchBasket
  {
   public void ImportPatch (File file) throws IOException,FileNotFoundException;
   public void ExportPatch (File file) throws IOException,FileNotFoundException;
   public void DeleteSelectedPatch();
   public void CopySelectedPatch();
   public void SendSelectedPatch();
   public void PlaySelectedPatch();
   public void StoreSelectedPatch();
   public JInternalFrame EditSelectedPatch();
   public void PastePatch();
   public ArrayList getPatchCollection();   //returns collection of all patches in basket
  }
