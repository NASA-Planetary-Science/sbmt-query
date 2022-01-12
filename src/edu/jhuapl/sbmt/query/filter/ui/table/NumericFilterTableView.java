package edu.jhuapl.sbmt.query.filter.ui.table;

import java.awt.Component;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.github.lgooddatepicker.tableeditors.DateTableEditor;
import com.github.lgooddatepicker.tableeditors.TimeTableEditor;

import edu.jhuapl.saavtk.gui.util.IconUtil;
import edu.jhuapl.saavtk.gui.util.ToolTipUtil;
import edu.jhuapl.sbmt.query.filter.model.FilterModel;
import edu.jhuapl.sbmt.query.filter.model.FilterType;

import glum.gui.GuiUtil;
import glum.gui.misc.BooleanCellEditor;
import glum.gui.misc.BooleanCellRenderer;
import glum.gui.panel.itemList.ItemHandler;
import glum.gui.panel.itemList.ItemListPanel;
import glum.gui.panel.itemList.ItemProcessor;
import glum.gui.panel.itemList.query.QueryComposer;

public class NumericFilterTableView extends JPanel
{
//	private SpectrumPopupMenu<S> spectrumPopupMenu;
	protected JTable numericFilters;
	protected JTable nonNumericFilters;
	protected JTable timeWindowFilters;

	// for numeric table
//	private FilterModel filterModel;
	private ItemListPanel<FilterType> filterILP;
	private ItemHandler<FilterType> filterTableHandler;

	// for non numeric table
//	private FilterModel filterModel2;
	private ItemListPanel<FilterType> filterILP2;
	private ItemHandler<FilterType> filterTableHandler2;

	// for time window table
//	private FilterModel filterModel3;
	private ItemListPanel<FilterType> filterILP3;
	private ItemHandler<FilterType> filterTableHandler3;

	private JButton addButton;
	private JComboBox<FilterType> filterCombo;
	private JButton removeFilterButton;

	public NumericFilterTableView(/*FilterModel model, FilterModel model2, FilterModel model3*/)
	{
	}

	public void setup(FilterModel filterModel, FilterModel filterModel2, FilterModel filterModel3)
	{
		numericFilters = buildTable(filterModel);
		nonNumericFilters = buildTable2(filterModel2);
		timeWindowFilters = buildTable3(filterModel3);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.X_AXIS));

		removeFilterButton = GuiUtil.formButton(null, IconUtil.getItemDel());
		removeFilterButton.setToolTipText(ToolTipUtil.getItemDel());

		comboPanel.add(removeFilterButton);

		JButton sqlDebug = new JButton("SQL");
		sqlDebug.addActionListener(e -> {
			String debugSQL = "";
			filterModel.getSQLQueryString().stream().reduce(debugSQL, (s1, s2) -> { return s1 + " AND " + s2;});
			String debugSQL2 = "";
			filterModel2.getSQLQueryString().stream().reduce(debugSQL2, (s1, s2) -> { return s1 + " AND " + s2;});
			String debugSQL3 = "";
			filterModel3.getSQLQueryString().stream().reduce(debugSQL3, (s1, s2) -> { return s1 + " AND " + s2;});
		});

		comboPanel.add(sqlDebug);

		comboPanel.add(Box.createHorizontalGlue());
		comboPanel.add(new JLabel("Add new filter:"));
		Vector<FilterType> registeredFilters = new Vector<FilterType>();
		registeredFilters.addAll(FilterType.getRegisteredFilters());
		filterCombo = new JComboBox<FilterType>(registeredFilters);
		comboPanel.add(filterCombo);
		addButton = new JButton("Add");
		comboPanel.add(addButton);
		add(comboPanel);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(new JLabel("Numeric Filters:"));
		panel.add(Box.createHorizontalGlue());
		add(panel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(150, 150));
		add(scrollPane);

		scrollPane.setViewportView(numericFilters);

		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));
		panel3.add(new JLabel("Time Window Filters"));
		panel3.add(Box.createHorizontalGlue());
		add(panel3);


		JScrollPane scrollPane3 = new JScrollPane();
		scrollPane3.setPreferredSize(new Dimension(150, 150));
		add(scrollPane3);

		scrollPane3.setViewportView(timeWindowFilters);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
		panel2.add(new JLabel("Other Filters:"));
		panel2.add(Box.createHorizontalGlue());
		add(panel2);


		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setPreferredSize(new Dimension(150, 150));
		add(scrollPane2);

		scrollPane2.setViewportView(nonNumericFilters);



	}

	private JTable buildTable(FilterModel filterModel)
	{
		// Table Content
		QueryComposer<FilterColumnLookup> tmpComposer = new QueryComposer<>();
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_ENABLED, Boolean.class, "Enabled", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_NAME, String.class, "Name", null);
//		tmpComposer.addAttribute(FilterColumnLookup.FILTER_TYPE , String.class, "Type", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_LOW, Boolean.class, "Low Value", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_HIGH, Boolean.class, "High Value", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_UNITS, Boolean.class, "Units", null);

		tmpComposer.setRenderer(FilterColumnLookup.FILTER_ENABLED, new BooleanCellRenderer());

//		tmpComposer.setRenderer(FilterColumnLookup.FILTER_LOW, new DateOrDefaultCellRenderer());
		tmpComposer.setEditor(FilterColumnLookup.FILTER_ENABLED, new BooleanCellEditor());
		tmpComposer.setEditor(FilterColumnLookup.FILTER_LOW, new DefaultCellEditor(new JTextField()));
		tmpComposer.setEditor(FilterColumnLookup.FILTER_HIGH, new DefaultCellEditor(new JTextField()));

		tmpComposer.getItem(FilterColumnLookup.FILTER_NAME).defaultSize *= 3;
//		tmpComposer.getItem(FilterColumnLookup.FILTER_TYPE).defaultSize *= 2;
		tmpComposer.getItem(FilterColumnLookup.FILTER_LOW).defaultSize *= 3;
		tmpComposer.getItem(FilterColumnLookup.FILTER_HIGH).defaultSize *= 3;
		tmpComposer.getItem(FilterColumnLookup.FILTER_UNITS).defaultSize *= 3;

		filterTableHandler = new NumericFilterItemHandler(filterModel, tmpComposer);
		ItemProcessor<FilterType> tmpIP = filterModel;
		filterILP = new ItemListPanel<>(filterTableHandler, tmpIP, true);
		filterILP.setSortingEnabled(true);
		JTable filterTable = filterILP.getTable();
		filterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

//		spectrumTable.addMouseListener(new SpectrumTablePopupListener<>(spectrumCollection, boundaryCollection,
//				spectrumPopupMenu, spectrumTable));


		return filterTable;
	}

	private JTable buildTable2(FilterModel filterModel2)
	{
		// Table Content
		QueryComposer<FilterColumnLookup> tmpComposer = new QueryComposer<>();
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_ENABLED, Boolean.class, "Enabled", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_NAME, Boolean.class, "Name", null);
//		tmpComposer.addAttribute(FilterColumnLookup.FILTER_TYPE , Boolean.class, "Type", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_RANGE , Boolean.class, "Value", null);

		tmpComposer.setRenderer(FilterColumnLookup.FILTER_ENABLED, new BooleanCellRenderer());

		tmpComposer.setEditor(FilterColumnLookup.FILTER_ENABLED, new BooleanCellEditor());
		tmpComposer.setEditor(FilterColumnLookup.FILTER_RANGE, new CustomComboBoxEditor(filterModel2));

		tmpComposer.getItem(FilterColumnLookup.FILTER_NAME).defaultSize *= 3;
//		tmpComposer.getItem(FilterColumnLookup.FILTER_TYPE).defaultSize *= 2;
		tmpComposer.getItem(FilterColumnLookup.FILTER_RANGE).defaultSize *= 5;

		filterTableHandler2 = new NumericFilterItemHandler(filterModel2, tmpComposer);
		ItemProcessor<FilterType> tmpIP = filterModel2;
		filterILP2 = new ItemListPanel<>(filterTableHandler2, tmpIP, true);
		filterILP2.setSortingEnabled(true);
		JTable filterTable = filterILP2.getTable();
		filterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		spectrumTable.addMouseListener(new SpectrumTablePopupListener<>(spectrumCollection, boundaryCollection,
//				spectrumPopupMenu, spectrumTable));


		return filterTable;
	}

	private JTable buildTable3(FilterModel filterModel3)
	{
		// Table Content
		QueryComposer<FilterColumnLookup> tmpComposer = new QueryComposer<>();
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_ENABLED, Boolean.class, "Enabled", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_NAME, Boolean.class, "Name", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_START_DATE, LocalDate.class, "Start Date", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_START_TIME, LocalTime.class, "Start Time", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_END_DATE, LocalDate.class, "End Date", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_END_TIME, LocalTime.class, "End Time", null);

		tmpComposer.setRenderer(FilterColumnLookup.FILTER_ENABLED, new BooleanCellRenderer());

		tmpComposer.setEditor(FilterColumnLookup.FILTER_ENABLED, new BooleanCellEditor());
		tmpComposer.setEditor(FilterColumnLookup.FILTER_START_DATE, new DateTableEditor());
		tmpComposer.setEditor(FilterColumnLookup.FILTER_START_TIME, new TimeTableEditor());
		tmpComposer.setEditor(FilterColumnLookup.FILTER_END_DATE, new DateTableEditor());
		tmpComposer.setEditor(FilterColumnLookup.FILTER_END_TIME, new TimeTableEditor());

		tmpComposer.getItem(FilterColumnLookup.FILTER_NAME).defaultSize *= 3;
		tmpComposer.getItem(FilterColumnLookup.FILTER_START_DATE).defaultSize *= 2;
		tmpComposer.getItem(FilterColumnLookup.FILTER_START_TIME).defaultSize *= 2;
		tmpComposer.getItem(FilterColumnLookup.FILTER_END_DATE).defaultSize *= 2;
		tmpComposer.getItem(FilterColumnLookup.FILTER_END_TIME).defaultSize *= 2;

		filterTableHandler3 = new NumericFilterItemHandler(filterModel3, tmpComposer);
		ItemProcessor<FilterType> tmpIP = filterModel3;
		filterILP3 = new ItemListPanel<>(filterTableHandler3, tmpIP, true);
		filterILP3.setSortingEnabled(true);
		JTable filterTable = filterILP3.getTable();
		filterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		spectrumTable.addMouseListener(new SpectrumTablePopupListener<>(spectrumCollection, boundaryCollection,
//				spectrumPopupMenu, spectrumTable));


		return filterTable;
	}

	public JTable getResultList()
	{
		return numericFilters;
	}

	/**
	 * @return the addButton
	 */
	public JButton getAddButton()
	{
		return addButton;
	}

	/**
	 * @return the filterCombo
	 */
	public JComboBox<FilterType> getFilterCombo()
	{
		return filterCombo;
	}

	/**
	 * @return the removeFilterButton
	 */
	public JButton getRemoveFilterButton()
	{
		return removeFilterButton;
	}

	class CustomComboBoxEditor extends DefaultCellEditor {

		private DefaultComboBoxModel model;
		private FilterModel filterModel;

		public CustomComboBoxEditor(FilterModel filterModel) {
			super(new JComboBox());
			this.filterModel = filterModel;
			this.model = (DefaultComboBoxModel)((JComboBox)getComponent()).getModel();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column)
		{
			model.removeAllElements();
			FilterType filter = filterModel.getAllItems().get(row);
			for (int i=0; i < filter.getRange().size(); i++)
			{
				model.addElement(filter.getRange().get(i));
			}
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
	}

	public static void main(String[] args)
	{
//		FilterModel model = new FilterModel();
//		List<FilterType> filters = Lists.newArrayList();
//		model.setAllItems(filters);
//
//		var numericTableView = new NumericFilterTableView(model, new FilterModel(), new FilterModel());
//
//		FilterModel model2 = new FilterModel();
//		List<FilterType> filters2 = Lists.newArrayList();
//		model2.setAllItems(filters2);
//
//		var nonNumericTableView = new NonNumericFilterTableView(model2);
//
//		BasicFrame frame = new BasicFrame();
//		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
//		frame.setSize(500, 500);
//		frame.getContentPane().add(numericTableView);
//		frame.getContentPane().add(Box.createVerticalStrut(5));
//		frame.getContentPane().add(nonNumericTableView);
//		frame.setVisible(true);
	}
}
