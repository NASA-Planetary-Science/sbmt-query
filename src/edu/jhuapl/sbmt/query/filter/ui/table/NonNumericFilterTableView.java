//package edu.jhuapl.sbmt.query.filter.ui.table;
//
//import java.awt.Component;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.Date;
//import java.util.List;
//import java.util.Vector;
//
//import javax.swing.Box;
//import javax.swing.BoxLayout;
//import javax.swing.DefaultCellEditor;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.border.TitledBorder;
//
//import com.github.davidmoten.guavamini.Lists;
//
//import edu.jhuapl.saavtk.gui.util.IconUtil;
//import edu.jhuapl.saavtk.gui.util.ToolTipUtil;
//import edu.jhuapl.saavtk2.gui.BasicFrame;
//import edu.jhuapl.sbmt.query.filter.model.FilterModel;
//import edu.jhuapl.sbmt.query.filter.model.FilterType;
//
//import glum.gui.GuiUtil;
//import glum.gui.panel.itemList.ItemHandler;
//import glum.gui.panel.itemList.ItemListPanel;
//import glum.gui.panel.itemList.ItemProcessor;
//import glum.gui.panel.itemList.query.QueryComposer;
//
//public class NonNumericFilterTableView extends JPanel
//{
////	private SpectrumPopupMenu<S> spectrumPopupMenu;
//	protected JTable resultList;
////	private JLabel resultsLabel;
//
//	// for table
//	private FilterModel filterModel;
//	private ItemListPanel<FilterType> filterILP;
//	private ItemHandler<FilterType> filterTableHandler;
//	private JButton addButton;
//	private JComboBox<FilterType> filterCombo;
//	private JButton removeFilterButton;
//
//	public NonNumericFilterTableView(FilterModel model)
//	{
//		this.filterModel = model;
//		init();
//		setup();
//	}
//
//	protected void init()
//	{
////		resultsLabel = new JLabel("0 Results");
//		resultList = buildTable();
//	}
//
//	public void setup()
//	{
//		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		setBorder(new TitledBorder(null, "Other Filters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		ActionListener removeListener = new ActionListener()
//		{
//
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				filterModel.removeItems(filterModel.getSelectedItems());
//
//			}
//		};
//		removeFilterButton = GuiUtil.formButton(removeListener, IconUtil.getItemDel());
//		removeFilterButton.setToolTipText(ToolTipUtil.getItemDel());
//
//		JPanel comboPanel = new JPanel();
//		comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.X_AXIS));
//		comboPanel.add(removeFilterButton);
//
//		JButton sqlDebug = new JButton("SQL");
//		sqlDebug.addActionListener(e -> {
//			String debugSQL = filterModel.getSQLQueryString();
//		});
//
//		comboPanel.add(sqlDebug);
//
//
//		comboPanel.add(Box.createHorizontalGlue());
//		comboPanel.add(new JLabel("Add new filter:"));
//		Vector<FilterType> registeredFilters = new Vector<FilterType>();
//		registeredFilters.addAll(FilterType.getRegisteredFilters().stream().filter(filter -> (filter.getType() != Double.class) && (filter.getType() != Date.class)).toList());
//		filterCombo = new JComboBox<FilterType>(registeredFilters);
//		comboPanel.add(filterCombo);
//
//		JButton addButton = new JButton("Add");
//		addButton.addActionListener(e -> {
//			filterModel.addFilter((FilterType)filterCombo.getSelectedItem());
//		});
//		comboPanel.add(addButton);
//		add(comboPanel);
//
//		add(comboPanel);
//		JScrollPane scrollPane = new JScrollPane();
//		scrollPane.setPreferredSize(new java.awt.Dimension(150, 150));
//		add(scrollPane);
//
//		scrollPane.setViewportView(resultList);
//
//		JPanel panel = new JPanel();
//		add(panel);
//		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
//
//	}
//
//	class CustomComboBoxEditor extends DefaultCellEditor {
//
//		private DefaultComboBoxModel model;
//		private FilterModel filterModel;
//
//		public CustomComboBoxEditor(FilterModel filterModel) {
//			super(new JComboBox());
//			this.filterModel = filterModel;
//			this.model = (DefaultComboBoxModel)((JComboBox)getComponent()).getModel();
//		}
//
//		@Override
//		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
//				int column)
//		{
//			model.removeAllElements();
//			FilterType filter = filterModel.getAllItems().get(row);
//			for (int i=0; i < filter.getRange().size(); i++)
//			{
//				model.addElement(filter.getRange().get(i));
//			}
//			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
//		}
//
//	}
//
//	private JTable buildTable()
//	{
//		// Table Content
//		QueryComposer<FilterColumnLookup> tmpComposer = new QueryComposer<>();
//		tmpComposer.addAttribute(FilterColumnLookup.FILTER_NAME, Boolean.class, "Name", null);
//		tmpComposer.addAttribute(FilterColumnLookup.FILTER_TYPE , Boolean.class, "Type", null);
//		tmpComposer.addAttribute(FilterColumnLookup.FILTER_RANGE , Boolean.class, "Value", null);
//
//		tmpComposer.setEditor(FilterColumnLookup.FILTER_RANGE, new CustomComboBoxEditor(filterModel));
//
//		tmpComposer.getItem(FilterColumnLookup.FILTER_NAME).defaultSize *= 3;
//		tmpComposer.getItem(FilterColumnLookup.FILTER_TYPE).defaultSize *= 2;
//		tmpComposer.getItem(FilterColumnLookup.FILTER_RANGE).defaultSize *= 5;
//
//		filterTableHandler = new NumericFilterItemHandler(filterModel, tmpComposer);
//		ItemProcessor<FilterType> tmpIP = filterModel;
//		filterILP = new ItemListPanel<>(filterTableHandler, tmpIP, true);
//		filterILP.setSortingEnabled(true);
//		JTable filterTable = filterILP.getTable();
//		filterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
////		spectrumTable.addMouseListener(new SpectrumTablePopupListener<>(spectrumCollection, boundaryCollection,
////				spectrumPopupMenu, spectrumTable));
//
//
//		return filterTable;
//	}
//
//	public JTable getResultList()
//	{
//		return resultList;
//	}
//
//	public static void main(String[] args)
//	{
//		FilterModel model = new FilterModel();
//		List<FilterType> filters = Lists.newArrayList();
//		filters.add(FilterType.EMISSION_ANGLE);
//		filters.add(FilterType.INCIDENCE_ANGLE);
//		filters.add(FilterType.PHASE_ANGLE);
//		filters.add(FilterType.SC_ALTITUDE);
//		filters.add(FilterType.SC_DISTANCE);
//		filters.add(FilterType.RESOLUTION);
//		model.setAllItems(filters);
//
//		var tableView = new NonNumericFilterTableView(model);
//
//		BasicFrame frame = new BasicFrame();
//		frame.setSize(500, 500);
//		frame.getContentPane().add(tableView);
//		frame.setVisible(true);
//	}
//}
