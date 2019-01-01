//
//Form.java
//Itpr_3Cmini
//UI
//
//Created by Li子青 on 2018/12/01.
//Copyright © 2018年 Li子青. All rights reserved.
//
package main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.undo.UndoManager;

import Tokens.TreeNode;
import util.*;

public class Form extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* font */
	private Font font = new Font("Courier New", Font.PLAIN, 15); // 编辑区
	private Font conAndErrFont = new Font("微软雅黑", Font.PLAIN, 14); // 控制台&错误列表
	private Font treeFont = new Font("微软雅黑", Font.PLAIN, 12); // 分析结果显示区

	private final static JStatusBar STATUSBAR = new JStatusBar();// 状态条:当前行号&列数
	private static int columnNum; // 控制台列数
	private static int rowNum; // 控制台行数
	private static int presentMaxRow;// 控制台最大行数
	private static int[] index = new int[] { 0, 0 };
	private static StyledDocument doc = null;

	private final static JToolBar TOOLBAR = new JToolBar();// 工具条
	private JButton newButton;
	private JButton openButton;
	private JButton saveButton;
	private JButton runButton;
	private JButton lexButton;
	private JButton parseButton;
	private JButton helpButton;
	private JTabbedPane tabbedPanel; // 词法分析语法分析结果显示面板
	private final static JFileTree FILETREE = new JFileTree(new JFileTree.ExtensionFilter("lnk"));// 文件浏览树
	private static JCloseableTabbedPane editTabbedPane; // Cmini程序文本编辑区
	public static JTabbedPane proAndConPanel; // 控制台和错误信息
	public static JTextPane consoleArea = new JTextPane(); // 控制台输出
	public static JTextArea problemArea = new JTextArea(); // 错误显示区

	private static HashMap<JScrollPane, StyleEditor> map = new HashMap<JScrollPane, StyleEditor>();
	private FileDialog filedialog_save, filedialog_load; // 保存和打开对话框
	private final UndoManager undo = new UndoManager(); // Undo管理器
	private UndoableEditListener undoHandler = new UndoHandler();

	private LexicalAnalyzer lexer;
	private SyntaxAnalyzer parser;
	private SemanticAnalyzer interpret;

	/* 文件过滤器 */
	FileFilter filter = new FileFilter() {
		public String getDescription() {
			return "Cmini program(*.cmm)";
		}

		public boolean accept(File file) {
			String tmp = file.getName().toLowerCase();
			if (tmp.endsWith(".cmm") || tmp.endsWith(".CMM")) {
				return true;
			}
			return false;
		}
	};

	public Form(String title) {
		super();
		setLayout(null);
		setTitle(title);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(FILETREE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 工具条
		newButton = new JButton("New", new ImageIcon(getClass().getResource("/images/new.png")));
		newButton.setToolTipText("NEW");
		openButton = new JButton("Open", new ImageIcon(getClass().getResource("/images/open.png")));
		openButton.setToolTipText("Open");
		saveButton = new JButton("Save", new ImageIcon(getClass().getResource("/images/save.png")));
		saveButton.setToolTipText("Save");
		lexButton = new JButton("Lexical Analysis", new ImageIcon(getClass().getResource("/images/lex.png")));
		lexButton.setToolTipText("Lexical Analysis");
		parseButton = new JButton("Syntax Analysis", new ImageIcon(getClass().getResource("/images/parse.png")));
		parseButton.setToolTipText("Syntax Analysis");
		runButton = new JButton("Run Program    ", new ImageIcon(getClass().getResource("/images/run.png")));
		runButton.setToolTipText("Run");
		helpButton = new JButton("Help", new ImageIcon(getClass().getResource("/images/help.png")));
		helpButton.setToolTipText("Help");
		TOOLBAR.setFloatable(false);
		TOOLBAR.addSeparator(new Dimension(5, 0));
		TOOLBAR.add(lexButton);
		TOOLBAR.addSeparator(new Dimension(5, 0));
		TOOLBAR.add(parseButton);
		TOOLBAR.addSeparator(new Dimension(5, 0));
		TOOLBAR.add(runButton);
		TOOLBAR.addSeparator(new Dimension(445, 0));
		TOOLBAR.add(newButton);
		TOOLBAR.addSeparator(new Dimension(5, 0));
		TOOLBAR.add(openButton);
		TOOLBAR.addSeparator(new Dimension(5, 0));
		TOOLBAR.add(saveButton);
		TOOLBAR.addSeparator(new Dimension(12, 20));
		TOOLBAR.add(helpButton);
		add(TOOLBAR);
		TOOLBAR.setBounds(0, 0, 1240, 35);
		TOOLBAR.setPreferredSize(getPreferredSize());

		// 文件保存和打开对话框
		filedialog_save = new FileDialog(this, "Save", FileDialog.SAVE);
		filedialog_save.setVisible(false);
		filedialog_load = new FileDialog(this, "Open", FileDialog.LOAD);
		filedialog_load.setVisible(false);
		filedialog_save.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				filedialog_save.setVisible(false);
			}
		});
		filedialog_load.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				filedialog_load.setVisible(false);
			}
		});

		// CMM文本编辑区
		editTabbedPane = new JCloseableTabbedPane();
		editTabbedPane.setFont(treeFont);
		final StyleEditor editor = new StyleEditor();
		editor.setFont(font);
		JScrollPane scrollPane = new JScrollPane(editor);
		TextLineNumber tln = new TextLineNumber(editor);
		scrollPane.setRowHeaderView(tln);

		editor.addCaretListener(new StatusListener());
		editor.getDocument().addUndoableEditListener(undoHandler);
		// 获得默认焦点
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent evt) {
				editor.requestFocus();
				STATUSBAR.setStatus(0, "Row: " + 1 + ", Column: " + 1);
			}
		});
		map.put(scrollPane, editor);
		editTabbedPane.add(scrollPane, "HelloWorld" + ".cmm");
		JPanel editPanel = new JPanel(null);
		JPanel editLabelPanel = new JPanel(new BorderLayout());
		///editLabelPanel.setBackground(Color.GRAY);
		editLabelPanel.setBackground(new Color(240,190,169));
		//f9ebd2  249 251 210   f0bea9  240 190  169
		// 控制条和错误列表区
		consoleArea.setEditable(false);
		problemArea.setRows(6);
		problemArea.setEditable(false);
		consoleArea.setFont(font);
		problemArea.setFont(conAndErrFont);
		proAndConPanel = new JTabbedPane();
		proAndConPanel.setFont(treeFont);
		proAndConPanel.add(new JScrollPane(consoleArea), "Console");
		proAndConPanel.add(new JScrollPane(problemArea), "Problems");
		editPanel.add(editLabelPanel);
		editPanel.add(editTabbedPane);
		editPanel.add(proAndConPanel);
		editLabelPanel.setBounds(0, 0, 815, 16);
		editTabbedPane.setBounds(0, 15, 815, 462);
		proAndConPanel.setBounds(0, 475, 815, 176);
		add(editPanel);
		editPanel.setBounds(0, TOOLBAR.getHeight(), 815, 686 - TOOLBAR.getHeight() - STATUSBAR.getHeight());

		// 词法分析结果显示区
		JScrollPane lexerPanel = new JScrollPane(null);
		JScrollPane parserPanel = new JScrollPane(null);
		tabbedPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPanel.setFont(treeFont);
		tabbedPanel.add(lexerPanel, "Lexical");
		tabbedPanel.add(parserPanel, "Syntax");
		JPanel resultPanel = new JPanel(new BorderLayout());
		JLabel resultLabel = new JLabel("    ");
		JPanel resultLabelPanel = new JPanel(new BorderLayout());
		resultLabel.setForeground(Color.WHITE);
		resultLabelPanel.add(resultLabel, BorderLayout.WEST);
		//resultLabelPanel.setBackground(Color.GRAY);
		resultLabelPanel.setBackground(new Color(240,190,169));
		resultPanel.add(resultLabelPanel, BorderLayout.NORTH);
		resultPanel.add(tabbedPanel, BorderLayout.CENTER);
		add(resultPanel);
		resultPanel.setBounds(editPanel.getWidth(), TOOLBAR.getHeight(), 1098 - editPanel.getWidth(),
				686 - TOOLBAR.getHeight() - STATUSBAR.getHeight());

		// 设置状态条
		STATUSBAR.addStatusCell(6666);
		add(STATUSBAR);
		STATUSBAR.setBounds(0, 685, 1100, 20);

		// 为FILETREE添加双击监听器，使其在双击一个文件时打开该文件
		FILETREE.setFont(treeFont);
		FILETREE.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String str = "", fileName = "";
					StringBuilder text = new StringBuilder();
					File file = FILETREE.getSelectFile();
					fileName = file.getName();
					if (file.isFile()) {
						if (fileName.endsWith(".cmm") || fileName.endsWith(".CMM") || fileName.endsWith(".txt")
								|| fileName.endsWith(".TXT") || fileName.endsWith(".java")) {
							try {
								FileReader file_reader = new FileReader(file);
								BufferedReader in = new BufferedReader(file_reader);
								while ((str = in.readLine()) != null)
									text.append(str + '\n');
								in.close();
								file_reader.close();
							} catch (IOException e2) {
							}
							create(fileName);
							editTabbedPane.setTitleAt(editTabbedPane.getComponentCount() - 1, fileName);
							map.get(editTabbedPane.getSelectedComponent()).setText(text.toString());
						}
					}
					setSize(getWidth(), getHeight());
				}
			}
		});

		doc = consoleArea.getStyledDocument();
		consoleArea.addKeyListener(new KeyAdapter() {
			// 按下某键
			public void keyPressed(KeyEvent e) {
				// 获得当前的行和列位置
				getCurrenRowAndCol();
				if (rowNum > presentMaxRow) {
					presentMaxRow = rowNum;
				}
				if (rowNum < presentMaxRow) {
					consoleArea.setCaretPosition(doc.getLength());
					getCurrenRowAndCol();
				}
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					consoleArea.setCaretPosition(doc.getLength());
				}
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (columnNum == 1) {
						setControlArea(Color.BLACK, false);
					}
				}
			}

			// 释放某键
			public void keyReleased(KeyEvent e) {
				// 获得当前的行和列位置
				getCurrenRowAndCol();
				if (rowNum > presentMaxRow) {
					presentMaxRow = rowNum;
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// 获得光标相对0行0列的位置
					int pos = consoleArea.getCaretPosition();
					index[0] = index[1];
					index[1] = pos;

					setControlArea(Color.BLACK, false);
				}
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (rowNum <= presentMaxRow)
						consoleArea.setEditable(true);
				}
			}
		});

		// 为工具条按钮添加事件监听器
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				create(null);
			}
		});
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				open();
			}
		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				run();
			}
		});
		lexButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lex();
			}
		});
		parseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parse();
			}
		});

		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(new JOptionPane(),
						"A simple compiler for Cmini.\n"
						+ "Editor: edit your Cmini program.\n"
						+ "Console: show runtime output.\n"
						+ "Problem: show the exception occurs in lexical analysis, syntax analysis and semantic analysis.\n"
						+ "Result: show the file tree of lexical analysis and syntax analysis.\n\n"
						+ "Writer: 李子青　ID: 2016302580300\n" + "2018-12-25　　\n",
						"Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	// 内部类：Undo管理
	class UndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			undo.addEdit(e.getEdit());
		}
	}

	// 内部类：控制状态条的显示
	class StatusListener implements CaretListener {
		public void caretUpdate(CaretEvent e) {
			StyleEditor temp = map.get(editTabbedPane.getSelectedComponent());
			try {
				int row = temp.getLineOfOffset(e.getDot());
				int column = e.getDot() - temp.getLineStartOffset(row);
				STATUSBAR.setStatus(0, "Row: " + (row + 1) + ", Column: " + (column + 1));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 词法分析：对程序的词法进行分析，输出分析结果
	public void lex() {
		StyleEditor textArea = map.get(editTabbedPane.getSelectedComponent());
		String text = textArea.getText();

		if (text.equals("")) {
			JOptionPane.showMessageDialog(new JPanel(), "ERROR: the input program is empty!");
		} else {
			lexer = new LexicalAnalyzer(text);
			TreeNode root = lexer.getRoot();
			DefaultTreeModel model = new DefaultTreeModel(root);
			JTree lexerTree = new JTree(model);
			lexerTree.setCellRenderer(new JTreeRenderer());
			lexerTree.setShowsRootHandles(true);
			lexerTree.setRootVisible(true);
			lexerTree.setFont(treeFont);

			tabbedPanel.setComponentAt(0, new JScrollPane(lexerTree));
			tabbedPanel.setSelectedIndex(0);
			problemArea.setText("***************Lexical Analysis Begin***************\n");
			problemArea.append(lexer.getError());
			problemArea.append("Lexical error number:" + lexer.getErrCount() + "\n\n");
			proAndConPanel.setSelectedIndex(1);
		}
	}

	// 语法分析：对程序的语法进行分析，并显示语法树
	public TreeNode parse() {
		lex();
		if (lexer.getErrCount() != 0) {
			JOptionPane.showMessageDialog(new JPanel(),
					"Exception occurs in the lexical analysis！Please modify the program first before the syntax analysis!",
					"Syntax Analysis", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		parser = new SyntaxAnalyzer(lexer.getTokens());
		TreeNode root = parser.getRoot();
		DefaultTreeModel model = new DefaultTreeModel(root);
		JTree parserTree = new JTree(model);
		// 设置该JTree使用自定义的节点绘制器
		parserTree.setCellRenderer(new JTreeRenderer());

		// 设置是否显示根节点的“展开/折叠”图标,默认是false
		parserTree.setShowsRootHandles(true);
		// 设置节点是否可见,默认是true
		parserTree.setRootVisible(true);
		// 设置字体
		parserTree.setFont(treeFont);
		problemArea.append("\n");
		problemArea.append("***************Syntax Analysis Begin***************\n");
		if (parser.getErrCount() != 0) {
			problemArea.append(parser.getError());
			problemArea.append("Syntax error number: " + parser.getErrCount() + "\n");
			JOptionPane.showMessageDialog(new JPanel(),
					"Exception occurs in the syntax analysis! Please modify the program！", "Syntax Analysis",
					JOptionPane.ERROR_MESSAGE);
		} else {
			problemArea.append("Syntax error number: " + parser.getErrCount() + "\n\n");
		}
		tabbedPanel.setComponentAt(1, new JScrollPane(parserTree));
		tabbedPanel.setSelectedIndex(1);
		proAndConPanel.setSelectedIndex(1);
		return root;
	}

	// 运行：分析并运行CMM程序，显示运行结果
	public void run() {
		consoleArea.setText(null);
		columnNum = 0;
		rowNum = 0;
		presentMaxRow = 0;
		index = new int[] { 0, 0 };
		TreeNode node = parse();
		if (lexer.getErrCount() != 0) {
			return;
		} else if (parser.getErrCount() != 0 || node == null) {
			return;
		} else {
			interpret = new SemanticAnalyzer(node);
			interpret.start();
		}
	}

	// 新建
	private void create(String filename) {
		if (filename == null) {
			filename = JOptionPane.showInputDialog("New Cmini file(.cmm)");
			if (filename == null || filename.equals("")) {
				JOptionPane.showMessageDialog(null, "File name can't be empty!");
				return;
			}
		}
		filename += ".cmm";
		StyleEditor editor = new StyleEditor();
		editor.setFont(font);
		JScrollPane scrollPane = new JScrollPane(editor);
		TextLineNumber tln = new TextLineNumber(editor);
		scrollPane.setRowHeaderView(tln);

		editor.addCaretListener(new StatusListener());
		editor.getDocument().addUndoableEditListener(undoHandler);
		map.put(scrollPane, editor);
		editTabbedPane.add(scrollPane, filename);
		editTabbedPane.setSelectedIndex(editTabbedPane.getTabCount() - 1);
	}

	// 打开
	private void open() {
		boolean isOpened = false;
		String str = "", fileName = "";
		File file = null;
		StringBuilder text = new StringBuilder();
		filedialog_load.setVisible(true);
		if (filedialog_load.getFile() != null) {
			try {
				file = new File(filedialog_load.getDirectory(), filedialog_load.getFile());
				fileName = file.getName();
				FileReader file_reader = new FileReader(file);
				BufferedReader in = new BufferedReader(file_reader);
				while ((str = in.readLine()) != null)
					text.append(str + '\n');
				in.close();
				file_reader.close();
			} catch (IOException e2) {
			}
			for (int i = 0; i < editTabbedPane.getComponentCount(); i++) {
				if (editTabbedPane.getTitleAt(i).equals(fileName)) {
					isOpened = true;
					editTabbedPane.setSelectedIndex(i);
				}
			}
			if (!isOpened) {
				create(fileName);
				editTabbedPane.setTitleAt(editTabbedPane.getComponentCount() - 1, fileName);
				map.get(editTabbedPane.getSelectedComponent()).setText(text.toString());
			}

		}
	}

	// 保存
	private void save() {
		StyleEditor temp = map.get(editTabbedPane.getSelectedComponent());
		if (temp.getText() != null) {
			filedialog_save.setVisible(true);
			if (filedialog_save.getFile() != null) {
				try {
					File file = new File(filedialog_save.getDirectory(), filedialog_save.getFile());
					FileWriter fw = new FileWriter(file);
					fw.write(map.get(editTabbedPane.getSelectedComponent()).getText());
					fw.close();
				} catch (IOException e2) {
				}
			}
		}
	}

	private void getCurrenRowAndCol() {
		int row = 0;
		int col = 0;
		// 获得光标相对0行0列的位置
		int pos = consoleArea.getCaretPosition();
		Element root = consoleArea.getDocument().getDefaultRootElement();
		int index = root.getElementIndex(doc.getParagraphElement(pos).getStartOffset());
		// 列
		try {
			col = pos - doc.getText(0, doc.getLength()).substring(0, pos).lastIndexOf("\n");
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		// 行
		try {
			// 返回行是从0算起的,所以+1
			row = Integer.parseInt(String.valueOf(index + 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		rowNum = row;
		columnNum = col;
		presentMaxRow = root.getElementIndex(doc.getParagraphElement(doc.getLength()).getStartOffset()) + 1;
	}

	// 改变controlArea的颜色与编辑属性
	public static void setControlArea(Color c, boolean edit) {
		proAndConPanel.setSelectedIndex(0);
		consoleArea.setFocusable(true);
		consoleArea.setForeground(c);
		consoleArea.setEditable(edit);
	}

	// 主函数
	public static void main(String[] args) {
		Form frame = new Form("Cmini Compiler");
		frame.setBounds(60, 0, 1100, 730);
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
