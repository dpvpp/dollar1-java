import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Canvas extends JPanel
{
	private static final long serialVersionUID = 1L;
	static JLabel label = new JLabel();
	static ArrayList<Point> points = new ArrayList<Point>();
	static ArrayList<Template> templates = new ArrayList<Template>(); 
	
	public static void main(String arg[]) 
	{	
		javax.swing.SwingUtilities.invokeLater(new Runnable() 
		{
            public void run() 
            {
                createAndShowGUI();
            }
        });
	}
	
	public static void createAndShowGUI()
	{
		LoadDefault();
		JFrame frame = new JFrame("$1");
		JButton load = new JButton("Load Template");
		load.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser fc = new JFileChooser();				
				fc.showDialog(frame, "Choose");
				if(fc.getSelectedFile()!=null)
				{
					File f = fc.getSelectedFile();
					try 
					{
						DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
						Document doc =  dBuilder.parse(f);
						ArrayList <Point>Tpoints = new ArrayList<Point>(); 
						NodeList list = doc.getElementsByTagName("Point");
						for (int i = 0; i<list.getLength(); i++)
						{
							Element p = (Element)list.item(i);
							int px = Integer.parseInt(p.getAttribute("X"));
							int py = Integer.parseInt(p.getAttribute("Y"));
							Tpoints.add(new Point(px,py));
						}
						String n = JOptionPane.showInputDialog("Enter Template Name");
						ArrayList<Point> newPoints = DollarMethods.Resample(Tpoints,64);
						double iangle = DollarMethods.IndicativeAngle(newPoints);
						newPoints = DollarMethods.RotateBy(newPoints, iangle);
						newPoints = DollarMethods.ScaleTo(newPoints, 250);
						newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
						templates.add(new Template(n, newPoints));
					}
					catch(Exception ex)
					{
						System.out.println("Cannot read file");
					}
				}
			}		
		});
		
		JButton add = new JButton("Add Template");
		add.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(!points.isEmpty())
				{
					String n = JOptionPane.showInputDialog("Enter Template Name");
					ArrayList<Point> newPoints = DollarMethods.Resample(points,64);
					double iangle = DollarMethods.IndicativeAngle(newPoints);
					newPoints = DollarMethods.RotateBy(newPoints, iangle);
					newPoints = DollarMethods.ScaleTo(newPoints, 250);
					newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
					templates.add(new Template(n, newPoints));
				}
				else
				{
					JOptionPane.showMessageDialog(frame, "No points were drawn");
				}
			}		
		});
		
		JButton rec = new JButton("Recognize");
		rec.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(!templates.isEmpty())
				{
					ArrayList<Point> newPoints = DollarMethods.Resample(points, 64);
					double iangle = DollarMethods.IndicativeAngle(newPoints);
					newPoints = DollarMethods.RotateBy(newPoints, iangle);
					newPoints = DollarMethods.ScaleTo(newPoints, 250);
					newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
					Result results = DollarMethods.Recognize(newPoints, templates);
					label.setText(results.getTemplate().getName() + " " + results.getScore());
				}
				else
				{
					label.setText("No Templates");
				}
			}		
		});
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 3));
		buttons.add(load);
		buttons.add(add);
		buttons.add(rec);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(500, 500);
		frame.add(new Canvas(),BorderLayout.CENTER);
		frame.add(label,BorderLayout.NORTH);
		frame.add(buttons,BorderLayout.SOUTH);
		frame.setVisible(true);
	}
	
	public Canvas()
	{		
		setBackground(Color.WHITE);
		setSize(480, 480);
		
		MouseListener mouseListener = new MouseListener() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{
				points = new ArrayList<Point>();
				Point point = new Point(e.getX(), e.getY());
				points.add(point);
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		};
		
		MouseMotionListener mouseMotionListener = new MouseMotionListener() 
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				Point point = new Point(e.getX(), e.getY());
				points.add(point);
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
			
		};
		
		label.addMouseListener(mouseListener);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		if (!points.isEmpty())
		{
			for (Point point:points)
			{
				g.fillOval((int)point.getX(), (int)point.getY(), 4, 4);
			}
		}
	}
	
	//Default templates
	public static void LoadDefault()//uses same points as javascript code
	{
		String name = "Triangle";
		ArrayList<Point> tpoints = new ArrayList<Point>(Arrays.asList(new Point(137,139),new Point(135,141),new Point(133,144),
				new Point(132,146),new Point(130,149),new Point(128,151),new Point(126,155),new Point(123,160),new Point(120,166),
				new Point(116,171),new Point(112,177),new Point(107,183),new Point(102,188),new Point(100,191),new Point(95,195),
				new Point(90,199),new Point(86,203),new Point(82,206),new Point(80,209),new Point(75,213),new Point(73,213),
				new Point(70,216),new Point(67,219),new Point(64,221),new Point(61,223),new Point(60,225),new Point(62,226),
				new Point(65,225),new Point(67,226),new Point(74,226),new Point(77,227),new Point(85,229),new Point(91,230),
				new Point(99,231),new Point(108,232),new Point(116,233),new Point(125,233),new Point(134,234),new Point(145,233),
				new Point(153,232),new Point(160,233),new Point(170,234),new Point(177,235),new Point(179,236),new Point(186,237),
				new Point(193,238),new Point(198,239),new Point(200,237),new Point(202,239),new Point(204,238),new Point(206,234),
				new Point(205,230),new Point(202,222),new Point(197,216),new Point(192,207),new Point(186,198),new Point(179,189),
				new Point(174,183),new Point(170,178),new Point(164,171),new Point(161,168),new Point(154,160),new Point(148,155),
				new Point(143,150),new Point(138,148),new Point(136,148)));
		ArrayList<Point> newPoints = DollarMethods.Resample(tpoints,64);
		double iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "X";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(87,142),new Point(89,145),new Point(91,148),new Point(93,151),
				new Point(96,155),new Point(98,157),new Point(100,160),new Point(102,162),new Point(106,167),new Point(108,169),
				new Point(110,171),new Point(115,177),new Point(119,183),new Point(123,189),new Point(127,193),new Point(129,196),
				new Point(133,200),new Point(137,206),new Point(140,209),new Point(143,212),new Point(146,215),new Point(151,220),
				new Point(153,222),new Point(155,223),new Point(157,225),new Point(158,223),new Point(157,218),new Point(155,211),
				new Point(154,208),new Point(152,200),new Point(150,189),new Point(148,179),new Point(147,170),new Point(147,158),
				new Point(147,148),new Point(147,141),new Point(147,136),new Point(144,135),new Point(142,137),new Point(140,139),
				new Point(135,145),new Point(131,152),new Point(124,163),new Point(116,177),new Point(108,191),new Point(100,206),
				new Point(94,217),new Point(91,222),new Point(89,225),new Point(87,226),new Point(87,224)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Rectangle";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(78,149),new Point(78,153),new Point(78,157),new Point(78,160),
				new Point(79,162),new Point(79,164),new Point(79,167),new Point(79,169),new Point(79,173),new Point(79,178),
				new Point(79,183),new Point(80,189),new Point(80,193),new Point(80,198),new Point(80,202),new Point(81,208),
				new Point(81,210),new Point(81,216),new Point(82,222),new Point(82,224),new Point(82,227),new Point(83,229),
				new Point(83,231),new Point(85,230),new Point(88,232),new Point(90,233),new Point(92,232),new Point(94,233),
				new Point(99,232),new Point(102,233),new Point(106,233),new Point(109,234),new Point(117,235),new Point(123,236),
				new Point(126,236),new Point(135,237),new Point(142,238),new Point(145,238),new Point(152,238),new Point(154,239),
				new Point(165,238),new Point(174,237),new Point(179,236),new Point(186,235),new Point(191,235),new Point(195,233),
				new Point(197,233),new Point(200,233),new Point(201,235),new Point(201,233),new Point(199,231),new Point(198,226),
				new Point(198,220),new Point(196,207),new Point(195,195),new Point(195,181),new Point(195,173),new Point(195,163),
				new Point(194,155),new Point(192,145),new Point(192,143),new Point(192,138),new Point(191,135),new Point(191,133),
				new Point(191,130),new Point(190,128),new Point(188,129),new Point(186,129),new Point(181,132),new Point(173,131),
				new Point(162,131),new Point(151,132),new Point(149,132),new Point(138,132),new Point(136,132),new Point(122,131),
				new Point(120,131),new Point(109,130),new Point(107,130),new Point(90,132),new Point(81,133),new Point(76,133)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Circle";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(127,141),new Point(124,140),new Point(120,139),new Point(118,139),
				new Point(116,139),new Point(111,140),new Point(109,141),new Point(104,144),new Point(100,147),new Point(96,152),
				new Point(93,157),new Point(90,163),new Point(87,169),new Point(85,175),new Point(83,181),new Point(82,190),
				new Point(82,195),new Point(83,200),new Point(84,205),new Point(88,213),new Point(91,216),new Point(96,219),
				new Point(103,222),new Point(108,224),new Point(111,224),new Point(120,224),new Point(133,223),new Point(142,222),
				new Point(152,218),new Point(160,214),new Point(167,210),new Point(173,204),new Point(178,198),new Point(179,196),
				new Point(182,188),new Point(182,177),new Point(178,167),new Point(170,150),new Point(163,138),new Point(152,130),
				new Point(143,129),new Point(140,131),new Point(129,136),new Point(126,139)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Check";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(91,185),new Point(93,185),new Point(95,185),new Point(97,185),
				new Point(100,188),new Point(102,189),new Point(104,190),new Point(106,193),new Point(108,195),new Point(110,198),
				new Point(112,201),new Point(114,204),new Point(115,207),new Point(117,210),new Point(118,212),new Point(120,214),
				new Point(121,217),new Point(122,219),new Point(123,222),new Point(124,224),new Point(126,226),new Point(127,229),
				new Point(129,231),new Point(130,233),new Point(129,231),new Point(129,228),new Point(129,226),new Point(129,224),
				new Point(129,221),new Point(129,218),new Point(129,212),new Point(129,208),new Point(130,198),new Point(132,189),
				new Point(134,182),new Point(137,173),new Point(143,164),new Point(147,157),new Point(151,151),new Point(155,144),
				new Point(161,137),new Point(165,131),new Point(171,122),new Point(174,118),new Point(176,114),new Point(177,112),
				new Point(177,114),new Point(175,116),new Point(173,118)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Caret";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(79,245),new Point(79,242),new Point(79,239),new Point(80,237),
				new Point(80,234),new Point(81,232),new Point(82,230),new Point(84,224),new Point(86,220),new Point(86,218),
				new Point(87,216),new Point(88,213),new Point(90,207),new Point(91,202),new Point(92,200),new Point(93,194),
				new Point(94,192),new Point(96,189),new Point(97,186),new Point(100,179),new Point(102,173),new Point(105,165),
				new Point(107,160),new Point(109,158),new Point(112,151),new Point(115,144),new Point(117,139),new Point(119,136),
				new Point(119,134),new Point(120,132),new Point(121,129),new Point(122,127),new Point(124,125),new Point(126,124),
				new Point(129,125),new Point(131,127),new Point(132,130),new Point(136,139),new Point(141,154),new Point(145,166),
				new Point(151,182),new Point(156,193),new Point(157,196),new Point(161,209),new Point(162,211),new Point(167,223),
				new Point(169,229),new Point(170,231),new Point(173,237),new Point(176,242),new Point(177,244),new Point(179,250),
				new Point(181,255),new Point(182,257)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Zig-zag";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(307,216),new Point(333,186),new Point(356,215),new Point(375,186),
				new Point(399,216),new Point(418,186)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Arrow";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(68,222),new Point(70,220),new Point(73,218),new Point(75,217),new Point(77,215),
				new Point(80,213),new Point(82,212),new Point(84,210),new Point(87,209),new Point(89,208),new Point(92,206),new Point(95,204),
				new Point(101,201),new Point(106,198),new Point(112,194),new Point(118,191),new Point(124,187),new Point(127,186),new Point(132,183),
				new Point(138,181),new Point(141,180),new Point(146,178),new Point(154,173),new Point(159,171),new Point(161,170),new Point(166,167),
				new Point(168,167),new Point(171,166),new Point(174,164),new Point(177,162),new Point(180,160),new Point(182,158),new Point(183,156),
				new Point(181,154),new Point(178,153),new Point(171,153),new Point(164,153),new Point(160,153),new Point(150,154),new Point(147,155),
				new Point(141,157),new Point(137,158),new Point(135,158),new Point(137,158),new Point(140,157),new Point(143,156),new Point(151,154),
				new Point(160,152),new Point(170,149),new Point(179,147),new Point(185,145),new Point(192,144),new Point(196,144),new Point(198,144),
				new Point(200,144),new Point(201,147),new Point(199,149),new Point(194,157),new Point(191,160),new Point(186,167),new Point(180,176),
				new Point(177,179),new Point(171,187),new Point(169,189),new Point(165,194),new Point(164,196)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Left square bracket";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(140,124),new Point(138,123),new Point(135,122),new Point(133,123),new Point(130,123),
				new Point(128,124),new Point(125,125),new Point(122,124),new Point(120,124),new Point(118,124),new Point(116,125),new Point(113,125),
				new Point(111,125),new Point(108,124),new Point(106,125),new Point(104,125),new Point(102,124),new Point(100,123),new Point(98,123),
				new Point(95,124),new Point(93,123),new Point(90,124),new Point(88,124),new Point(85,125),new Point(83,126),new Point(81,127),
				new Point(81,129),new Point(82,131),new Point(82,134),new Point(83,138),new Point(84,141),new Point(84,144),new Point(85,148),
				new Point(85,151),new Point(86,156),new Point(86,160),new Point(86,164),new Point(86,168),new Point(87,171),new Point(87,175),
				new Point(87,179),new Point(87,182),new Point(87,186),new Point(88,188),new Point(88,195),new Point(88,198),new Point(88,201),
				new Point(88,207),new Point(89,211),new Point(89,213),new Point(89,217),new Point(89,222),new Point(88,225),new Point(88,229),
				new Point(88,231),new Point(88,233),new Point(88,235),new Point(89,237),new Point(89,240),new Point(89,242),new Point(91,241),
				new Point(94,241),new Point(96,240),new Point(98,239),new Point(105,240),new Point(109,240),new Point(113,239),new Point(116,240),
				new Point(121,239),new Point(130,240),new Point(136,237),new Point(139,237),new Point(144,238),new Point(151,237),new Point(157,236),
				new Point(159,237)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Right square bracket";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(112,138),new Point(112,136),new Point(115,136),new Point(118,137),new Point(120,136),
				new Point(123,136),new Point(125,136),new Point(128,136),new Point(131,136),new Point(134,135),new Point(137,135),new Point(140,134),
				new Point(143,133),new Point(145,132),new Point(147,132),new Point(149,132),new Point(152,132),new Point(153,134),new Point(154,137),
				new Point(155,141),new Point(156,144),new Point(157,152),new Point(158,161),new Point(160,170),new Point(162,182),new Point(164,192),
				new Point(166,200),new Point(167,209),new Point(168,214),new Point(168,216),new Point(169,221),new Point(169,223),new Point(169,228),
				new Point(169,231),new Point(166,233),new Point(164,234),new Point(161,235),new Point(155,236),new Point(147,235),new Point(140,233),
				new Point(131,233),new Point(124,233),new Point(117,235),new Point(114,238),new Point(112,238)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "V";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(89,164),new Point(90,162),new Point(92,162),new Point(94,164),new Point(95,166),
				new Point(96,169),new Point(97,171),new Point(99,175),new Point(101,178),new Point(103,182),new Point(106,189),new Point(108,194),
				new Point(111,199),new Point(114,204),new Point(117,209),new Point(119,214),new Point(122,218),new Point(124,222),new Point(126,225),
				new Point(128,228),new Point(130,229),new Point(133,233),new Point(134,236),new Point(136,239),new Point(138,240),new Point(139,242),
				new Point(140,244),new Point(142,242),new Point(142,240),new Point(142,237),new Point(143,235),new Point(143,233),new Point(145,229),
				new Point(146,226),new Point(148,217),new Point(149,208),new Point(149,205),new Point(151,196),new Point(151,193),new Point(153,182),
				new Point(155,172),new Point(157,165),new Point(159,160),new Point(162,155),new Point(164,150),new Point(165,148),new Point(166,146)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Delete";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(123,129),new Point(123,131),new Point(124,133),new Point(125,136),new Point(127,140),
				new Point(129,142),new Point(133,148),new Point(137,154),new Point(143,158),new Point(145,161),new Point(148,164),new Point(153,170),
				new Point(158,176),new Point(160,178),new Point(164,183),new Point(168,188),new Point(171,191),new Point(175,196),new Point(178,200),
				new Point(180,202),new Point(181,205),new Point(184,208),new Point(186,210),new Point(187,213),new Point(188,215),new Point(186,212),
				new Point(183,211),new Point(177,208),new Point(169,206),new Point(162,205),new Point(154,207),new Point(145,209),new Point(137,210),
				new Point(129,214),new Point(122,217),new Point(118,218),new Point(111,221),new Point(109,222),new Point(110,219),new Point(112,217),
				new Point(118,209),new Point(120,207),new Point(128,196),new Point(135,187),new Point(138,183),new Point(148,167),new Point(157,153),
				new Point(163,145),new Point(165,142),new Point(172,133),new Point(177,127),new Point(179,127),new Point(180,125)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Left curly brace";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(150,116),new Point(147,117),new Point(145,116),new Point(142,116),new Point(139,117),
				new Point(136,117),new Point(133,118),new Point(129,121),new Point(126,122),new Point(123,123),new Point(120,125),new Point(118,127),
				new Point(115,128),new Point(113,129),new Point(112,131),new Point(113,134),new Point(115,134),new Point(117,135),new Point(120,135),
				new Point(123,137),new Point(126,138),new Point(129,140),new Point(135,143),new Point(137,144),new Point(139,147),new Point(141,149),
				new Point(140,152),new Point(139,155),new Point(134,159),new Point(131,161),new Point(124,166),new Point(121,166),new Point(117,166),
				new Point(114,167),new Point(112,166),new Point(114,164),new Point(116,163),new Point(118,163),new Point(120,162),new Point(122,163),
				new Point(125,164),new Point(127,165),new Point(129,166),new Point(130,168),new Point(129,171),new Point(127,175),new Point(125,179),
				new Point(123,184),new Point(121,190),new Point(120,194),new Point(119,199),new Point(120,202),new Point(123,207),new Point(127,211),
				new Point(133,215),new Point(142,219),new Point(148,220),new Point(151,221)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Right curly brace";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(117,132),new Point(115,132),new Point(115,129),new Point(117,129),new Point(119,128),
				new Point(122,127),new Point(125,127),new Point(127,127),new Point(130,127),new Point(133,129),new Point(136,129),new Point(138,130),
				new Point(140,131),new Point(143,134),new Point(144,136),new Point(145,139),new Point(145,142),new Point(145,145),new Point(145,147),
				new Point(145,149),new Point(144,152),new Point(142,157),new Point(141,160),new Point(139,163),new Point(137,166),new Point(135,167),
				new Point(133,169),new Point(131,172),new Point(128,173),new Point(126,176),new Point(125,178),new Point(125,180),new Point(125,182),
				new Point(126,184),new Point(128,187),new Point(130,187),new Point(132,188),new Point(135,189),new Point(140,189),new Point(145,189),
				new Point(150,187),new Point(155,186),new Point(157,185),new Point(159,184),new Point(156,185),new Point(154,185),new Point(149,185),
				new Point(145,187),new Point(141,188),new Point(136,191),new Point(134,191),new Point(131,192),new Point(129,193),new Point(129,195),
				new Point(129,197),new Point(131,200),new Point(133,202),new Point(136,206),new Point(139,211),new Point(142,215),new Point(145,220),
				new Point(147,225),new Point(148,231),new Point(147,239),new Point(144,244),new Point(139,248),new Point(134,250),new Point(126,253),
				new Point(119,253),new Point(115,253)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Star";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(75,250),new Point(75,247),new Point(77,244),new Point(78,242),new Point(79,239),
				new Point(80,237),new Point(82,234),new Point(82,232),new Point(84,229),new Point(85,225),new Point(87,222),new Point(88,219),
				new Point(89,216),new Point(91,212),new Point(92,208),new Point(94,204),new Point(95,201),new Point(96,196),new Point(97,194),
				new Point(98,191),new Point(100,185),new Point(102,178),new Point(104,173),new Point(104,171),new Point(105,164),new Point(106,158),
				new Point(107,156),new Point(107,152),new Point(108,145),new Point(109,141),new Point(110,139),new Point(112,133),new Point(113,131),
				new Point(116,127),new Point(117,125),new Point(119,122),new Point(121,121),new Point(123,120),new Point(125,122),new Point(125,125),
				new Point(127,130),new Point(128,133),new Point(131,143),new Point(136,153),new Point(140,163),new Point(144,172),new Point(145,175),
				new Point(151,189),new Point(156,201),new Point(161,213),new Point(166,225),new Point(169,233),new Point(171,236),new Point(174,243),
				new Point(177,247),new Point(178,249),new Point(179,251),new Point(180,253),new Point(180,255),new Point(179,257),new Point(177,257),
				new Point(174,255),new Point(169,250),new Point(164,247),new Point(160,245),new Point(149,238),new Point(138,230),new Point(127,221),
				new Point(124,220),new Point(112,212),new Point(110,210),new Point(96,201),new Point(84,195),new Point(74,190),new Point(64,182),
				new Point(55,175),new Point(51,172),new Point(49,170),new Point(51,169),new Point(56,169),new Point(66,169),new Point(78,168),
				new Point(92,166),new Point(107,164),new Point(123,161),new Point(140,162),new Point(156,162),new Point(171,160),new Point(173,160),
				new Point(186,160),new Point(195,160),new Point(198,161),new Point(203,163),new Point(208,163),new Point(206,164),new Point(200,167),
				new Point(187,172),new Point(174,179),new Point(172,181),new Point(153,192),new Point(137,201),new Point(123,211),new Point(112,220),
				new Point(99,229),new Point(90,237),new Point(80,244),new Point(73,250),new Point(69,254),new Point(69,252)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
		
		name = "Pigtail";
		tpoints = new ArrayList<Point>(Arrays.asList(new Point(81,219),new Point(84,218),new Point(86,220),new Point(88,220),new Point(90,220),
				new Point(92,219),new Point(95,220),new Point(97,219),new Point(99,220),new Point(102,218),new Point(105,217),new Point(107,216),
				new Point(110,216),new Point(113,214),new Point(116,212),new Point(118,210),new Point(121,208),new Point(124,205),new Point(126,202),
				new Point(129,199),new Point(132,196),new Point(136,191),new Point(139,187),new Point(142,182),new Point(144,179),new Point(146,174),
				new Point(148,170),new Point(149,168),new Point(151,162),new Point(152,160),new Point(152,157),new Point(152,155),new Point(152,151),
				new Point(152,149),new Point(152,146),new Point(149,142),new Point(148,139),new Point(145,137),new Point(141,135),new Point(139,135),
				new Point(134,136),new Point(130,140),new Point(128,142),new Point(126,145),new Point(122,150),new Point(119,158),new Point(117,163),
				new Point(115,170),new Point(114,175),new Point(117,184),new Point(120,190),new Point(125,199),new Point(129,203),new Point(133,208),
				new Point(138,213),new Point(145,215),new Point(155,218),new Point(164,219),new Point(166,219),new Point(177,219),new Point(182,218),
				new Point(192,216),new Point(196,213),new Point(199,212),new Point(201,211)));
		newPoints = DollarMethods.Resample(tpoints,64);
		iangle = DollarMethods.IndicativeAngle(newPoints);
		newPoints = DollarMethods.RotateBy(newPoints,iangle);
		newPoints = DollarMethods.ScaleTo(newPoints, 250);
		newPoints = DollarMethods.TranslateTo(newPoints, new Point(0,0));
		templates.add(new Template(name,newPoints));
	}
}
