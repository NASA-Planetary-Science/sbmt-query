package edu.jhuapl.sbmt.query.filter.ui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.github.davidmoten.guavamini.Lists;

import edu.jhuapl.saavtk.gui.util.IconUtil;
import edu.jhuapl.saavtk.gui.util.ToolTipUtil;
import edu.jhuapl.saavtk2.gui.BasicFrame;
import edu.jhuapl.sbmt.gui.table.EphemerisTimeRenderer;
import edu.jhuapl.sbmt.query.filter.model.FilterModel;
import edu.jhuapl.sbmt.query.filter.model.FilterType;

import glum.gui.GuiUtil;
import glum.gui.panel.itemList.ItemHandler;
import glum.gui.panel.itemList.ItemListPanel;
import glum.gui.panel.itemList.ItemProcessor;
import glum.gui.panel.itemList.query.QueryComposer;

public class NumericFilterTableView extends JPanel
{
//	private SpectrumPopupMenu<S> spectrumPopupMenu;
	protected JTable resultList;
//	private JLabel resultsLabel;

	// for table
	private FilterModel filterModel;
	private ItemListPanel<FilterType> filterILP;
	private ItemHandler<FilterType> filterTableHandler;
	private JButton addButton;
	private JComboBox<FilterType> filterCombo;
	private JButton removeFilterButton;

	public NumericFilterTableView(FilterModel model)
	{
		this.filterModel = model;
		init();
		setup();
	}

	protected void init()
	{
//		resultsLabel = new JLabel("0 Results");
		resultList = buildTable();
	}

	public void setup()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder(null, "Numeric Filters", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.X_AXIS));

		ActionListener removeListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				filterModel.removeItems(filterModel.getSelectedItems());

			}
		};
		removeFilterButton = GuiUtil.formButton(removeListener, IconUtil.getItemDel());
		removeFilterButton.setToolTipText(ToolTipUtil.getItemDel());

		comboPanel.add(removeFilterButton);

		JButton sqlDebug = new JButton("SQL");
		sqlDebug.addActionListener(e -> {
			String debugSQL = filterModel.getSQLQueryString();
		});

		comboPanel.add(sqlDebug);

		comboPanel.add(Box.createHorizontalGlue());
		comboPanel.add(new JLabel("Add new filter:"));
		Vector<FilterType> registeredFilters = new Vector<FilterType>();
		registeredFilters.addAll(FilterType.getRegisteredFilters().stream().filter(filter -> filter.getType() == Double.class || filter.getType() == Date.class).toList());
		filterCombo = new JComboBox<FilterType>(registeredFilters);
		comboPanel.add(filterCombo);
		JButton addButton = new JButton("Add");
		addButton.addActionListener(e -> {
			filterModel.addFilter((FilterType)filterCombo.getSelectedItem());
		});
		comboPanel.add(addButton);
		add(comboPanel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new java.awt.Dimension(150, 150));
		add(scrollPane);

		scrollPane.setViewportView(resultList);

		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

	}

	private JTable buildTable()
	{
		// Table Content
		QueryComposer<FilterColumnLookup> tmpComposer = new QueryComposer<>();
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_NAME, Boolean.class, "Name", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_TYPE , Boolean.class, "Type", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_LOW, Boolean.class, "Low Value", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_HIGH, Boolean.class, "High Value", null);
		tmpComposer.addAttribute(FilterColumnLookup.FILTER_UNITS, Boolean.class, "Units", null);


		EphemerisTimeRenderer tmpTimeRenderer = new EphemerisTimeRenderer(false);
		tmpComposer.setEditor(FilterColumnLookup.FILTER_LOW, new DefaultCellEditor(new JTextField()));
		tmpComposer.setEditor(FilterColumnLookup.FILTER_HIGH, new DefaultCellEditor(new JTextField()));

		tmpComposer.getItem(FilterColumnLookup.FILTER_NAME).defaultSize *= 3;
		tmpComposer.getItem(FilterColumnLookup.FILTER_TYPE).defaultSize *= 2;
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

	public JTable getResultList()
	{
		return resultList;
	}

	public static void main(String[] args)
	{
		FilterModel model = new FilterModel();
		List<FilterType> filters = Lists.newArrayList();
		model.setAllItems(filters);

		var numericTableView = new NumericFilterTableView(model);

		FilterModel model2 = new FilterModel();
		List<FilterType> filters2 = Lists.newArrayList();
		model2.setAllItems(filters2);

		var nonNumericTableView = new NonNumericFilterTableView(model2);

		BasicFrame frame = new BasicFrame();
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setSize(500, 500);
		frame.getContentPane().add(numericTableView);
		frame.getContentPane().add(Box.createVerticalStrut(5));
		frame.getContentPane().add(nonNumericTableView);
		frame.setVisible(true);
	}
}
