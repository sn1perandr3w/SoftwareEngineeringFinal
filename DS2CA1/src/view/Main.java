package view;

import disjointSets.PixelNode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Main extends Application implements EventHandler<ActionEvent> {

	Stage window;
	FXMLLoader loader;

	public ArrayList<PixelNode> pixelList = new ArrayList<PixelNode>();
	public ArrayList<PixelNode> rootList = new ArrayList<PixelNode>();

	private Image loadedImage;
	private Image loadedImageGreyscaled;

	@FXML
	private Label fileName;

	@FXML
	private Label individualSheepLabel;

	@FXML
	private Label sheepGroupsLabel;

	@FXML
	private Label totalSheepLabel;

	@FXML
	private TextField inputNoiseThreshold;

	@FXML
	private TextField inputSheepSize;

	@FXML
	private ImageView greyscaleImage = new ImageView();

	public double noiseThreshold = 0.7;
	public int sheepSize = 2;
	public int sheepGroups;
	public int individualSheep;
	public int sheepFromGroups;

	public static void main(String[] args) {

		launch(args);

	}

	@Override
	public void handle(ActionEvent event) {
		String buttonPressed = ((Button) event.getSource()).getId();

		// GREYSCALING
		if (buttonPressed.equals("greyScale")) {
			noiseThreshold = Float.parseFloat(inputNoiseThreshold.getText());
			PixelReader pixelReader = loadedImage.getPixelReader();

			WritableImage writableGSImage = new WritableImage((int) loadedImage.getWidth(),
					(int) loadedImage.getHeight());

			PixelWriter pixelWriter = writableGSImage.getPixelWriter();

			for (int yPos = 0; yPos < writableGSImage.getHeight(); yPos++) {
				for (int xPos = 0; xPos < writableGSImage.getWidth(); xPos++) {
					Color color = pixelReader.getColor(xPos, yPos);

					double red = color.getRed();
					double green = color.getGreen();
					double blue = color.getBlue();
					double alpha = 1;
					double avg = (red + green + blue) / 3;

					if (avg < noiseThreshold) {
						avg = 0;
					}

					Color avgColor = new Color(avg, avg, avg, alpha);
					pixelWriter.setColor(xPos, yPos, avgColor);
				}
			}

			greyscaleImage.setImage(writableGSImage);

			Image newImage;
			newImage = greyscaleImage.getImage();
			loadedImageGreyscaled = newImage;
		}

		if (buttonPressed.equals("countSheep")) {
			sheepSize = Integer.parseInt(inputSheepSize.getText());

			noiseThreshold = Float.parseFloat(inputNoiseThreshold.getText());
			PixelReader pixelReader = loadedImageGreyscaled.getPixelReader();
			pixelList = pixelList = new ArrayList<PixelNode>();
			WritableImage writableGSImage = new WritableImage((int) loadedImageGreyscaled.getWidth(),
					(int) loadedImageGreyscaled.getHeight());
			for (int yPos = 0; yPos < writableGSImage.getHeight(); yPos++) {
				for (int xPos = 0; xPos < writableGSImage.getWidth(); xPos++) {
					Color color = pixelReader.getColor(xPos, yPos);

					double red = color.getRed();
					double green = color.getGreen();
					double blue = color.getBlue();
					double avg = (red + green + blue) / 3;

					if (avg > noiseThreshold) {
						pixelList.add(new PixelNode(xPos, yPos, color));
					}
				}
			}

		}

		if (buttonPressed.equals("markSheep")) {
			PixelReader pixelReader = loadedImageGreyscaled.getPixelReader();

			WritableImage writableGSImage = new WritableImage((int) loadedImageGreyscaled.getWidth(),
					(int) loadedImageGreyscaled.getHeight());

			PixelWriter pixelWriter = writableGSImage.getPixelWriter();

			for (int yPos = 0; yPos < writableGSImage.getHeight(); yPos++) {

				for (int xPos = 0; xPos < writableGSImage.getWidth(); xPos++) {
					Color color = pixelReader.getColor(xPos, yPos);
					// Setting unedited pixels
					for (int i = 0; i < pixelList.size(); i++) {

						pixelWriter.setColor(xPos, yPos, color);
					}

					// Branch creation from roots.
					for (int i = 0; i < pixelList.size(); i++) {
						PixelNode node = pixelList.get(i);
						if (node.getXpos() == xPos && node.getYpos() == yPos) {
							if (node.getParentID() == -1) {
								node.setParentID(node.getUID());
								node.setRoot(node.getParentID());
								rootList.add(node);

							}

							for (int j = 0; j < pixelList.size(); j++) {
								PixelNode relatedNode = pixelList.get(j);
								if (relatedNode.getXpos() == node.getXpos() + 1
										&& relatedNode.getYpos() == node.getYpos()
										|| relatedNode.getXpos() == node.getXpos()
												&& relatedNode.getYpos() == node.getYpos() + 1
										|| relatedNode.getXpos() == node.getXpos() + 1
												&& relatedNode.getYpos() == node.getYpos() + 1
										|| relatedNode.getXpos() == node.getXpos() - 1
												&& relatedNode.getYpos() == node.getYpos() + 1) {
									relatedNode.setParentID(node.getUID());
									relatedNode.setRoot(node.getRoot());
								}

							}

						}
					}

					// CREATING BOUNDARIES AROUND ROOTS AND THEIR CHILDREN

					for (int i = 0; i < pixelList.size(); i++) {
						int matches = 0;
						PixelNode node = pixelList.get(i);
						if (node.getRoot() == node.getUID()) {
							node.setMaxX(node.getXpos());
							node.setMinX(node.getXpos());
							node.setMaxY(node.getYpos());
							node.setMinY(node.getYpos());

							for (int j = 0; j < pixelList.size(); j++) {
								PixelNode nodeRelation = pixelList.get(j);
								if (nodeRelation.getRoot() == node.getUID() && node != nodeRelation) {
									if (nodeRelation.getXpos() > node.getMaxX()) {

										node.setMaxX(nodeRelation.getXpos());

									}
									if (nodeRelation.getXpos() < node.getMinX()) {

										node.setMinX(nodeRelation.getXpos());

									}
									if (nodeRelation.getYpos() > node.getMaxY()) {

										node.setMaxY(nodeRelation.getYpos());

									}
									if (nodeRelation.getYpos() < node.getMinY()) {

										node.setMinY(nodeRelation.getYpos());

									}
								}

								if (nodeRelation.getRoot() == node.getUID() && node != nodeRelation) {
									matches = matches + 1;
								}

							}

						}

						node.addChildCount(matches);

					}

				}
			}

			markImage(pixelWriter, writableGSImage);
		}

		if (buttonPressed.equals("markSheepNormal")) {

			PixelReader pixelReader = loadedImage.getPixelReader();

			WritableImage writableImage = new WritableImage((int) loadedImage.getWidth(),
					(int) loadedImage.getHeight());

			PixelWriter pixelWriter = writableImage.getPixelWriter();

			drawOriginal(writableImage, pixelReader, pixelWriter);
			markImage(pixelWriter, writableImage);
		}

		if (buttonPressed.equals("revertImage")) {
			PixelReader pixelReader = loadedImage.getPixelReader();

			WritableImage writableImage = new WritableImage((int) loadedImage.getWidth(),
					(int) loadedImage.getHeight());

			PixelWriter pixelWriter = writableImage.getPixelWriter();

			drawOriginal(writableImage, pixelReader, pixelWriter);

		}

	}

	public void exit() {
		System.exit(0);
	}

	public void handleMenu(ActionEvent event) {
		String menuPressed = ((MenuItem) event.getSource()).getId();

		if (menuPressed.equals("openMenu")) {

			FileChooser fileChooser = new FileChooser();

			FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
			FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
			fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

			fileChooser.setTitle("Open Image File");
			File file = fileChooser.showOpenDialog(window);
			Image newImage;
			try {
				newImage = new Image(file.toURI().toURL().toExternalForm());
				loadedImage = newImage;
				fileName.setText("Loaded");
				greyscaleImage.setImage(loadedImage);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

		}

		if (menuPressed.equals("exit")) {
			exit();
		}
	}

	public void drawOriginal(WritableImage writableImage, PixelReader pixelReader, PixelWriter pixelWriter) {

		for (int yPos = 0; yPos < writableImage.getHeight(); yPos++) {

			for (int xPos = 0; xPos < writableImage.getWidth(); xPos++) {
				Color color = pixelReader.getColor(xPos, yPos);
				// Setting unedited pixels
				for (int i = 0; i < pixelList.size(); i++) {

					pixelWriter.setColor(xPos, yPos, color);
				}
			}
		}
		greyscaleImage.setImage(writableImage);
	}

	public void markImage(PixelWriter pixelWriter, WritableImage writableGSImage) {
		sheepFromGroups = 0;
		individualSheep = 0;
		sheepGroups = 0;

		// MARKING BOUNDARIES

		for (int k = 0; k < pixelList.size(); k++) {
			Color markIndividual = new Color(1, 0, 0, 1);
			Color markGroup = new Color(1, 0, 1, 1);

			PixelNode rootNode = pixelList.get(k);

			int xPos = rootNode.getXpos();
			int yPos = rootNode.getYpos();

			if (rootNode.getChildCount() + 1 > sheepSize) {
				sheepFromGroups = sheepFromGroups + rootNode.getChildCount() + 1;

				// TOP AND LEFT

				for (int j = rootNode.getMinX() - 1; j < rootNode.getMaxX() + 2; j++) {

					pixelWriter.setColor(j, rootNode.getMinY() - 1, markGroup);
				}
				for (int jk = rootNode.getMinY() - 1; jk < rootNode.getMaxY() + 2; jk++) {
					pixelWriter.setColor(rootNode.getMinX() - 1, jk, markGroup);
				}

				// BOTTOM AND RIGHT

				for (int j = rootNode.getMinX() - 1; j < rootNode.getMaxX() + 2; j++) {

					pixelWriter.setColor(j, rootNode.getMaxY() + 1, markGroup);
				}
				for (int jk = rootNode.getMinY() - 1; jk < rootNode.getMaxY() + 2; jk++) {
					pixelWriter.setColor(rootNode.getMaxX() + 1, jk, markGroup);
				}

				sheepGroups = sheepGroups + 1;

			} else if (rootNode.getChildCount() + 1 == sheepSize && sheepSize != 1) {

				// TOP AND LEFT

				for (int j = rootNode.getMinX() - 1; j < rootNode.getMaxX() + 2; j++) {

					pixelWriter.setColor(j, rootNode.getMinY() - 1, markIndividual);
				}
				for (int jk = rootNode.getMinY() - 1; jk < rootNode.getMaxY() + 2; jk++) {
					pixelWriter.setColor(rootNode.getMinX() - 1, jk, markIndividual);
				}

				// BOTTOM AND RIGHT

				for (int j = rootNode.getMinX() - 1; j < rootNode.getMaxX() + 2; j++) {

					pixelWriter.setColor(j, rootNode.getMaxY() + 1, markIndividual);
				}
				for (int jk = rootNode.getMinY() - 1; jk < rootNode.getMaxY() + 2; jk++) {
					pixelWriter.setColor(rootNode.getMaxX() + 1, jk, markIndividual);
				}
				individualSheep = individualSheep + 1;

			} else

			if (rootNode.getChildCount() == 0 && rootNode.getParentID() == rootNode.getUID() && sheepSize == 1) {
				individualSheep = individualSheep + 1;
				for (int jkl = 0; jkl < rootNode.getMaxX(); jkl++) {
					pixelWriter.setColor(xPos - 1, yPos - 1, markIndividual);
					pixelWriter.setColor(xPos - 1, yPos, markIndividual);
					pixelWriter.setColor(xPos - 1, yPos + 1, markIndividual);
					pixelWriter.setColor(xPos, yPos + 1, markIndividual);
					pixelWriter.setColor(xPos, yPos - 1, markIndividual);
					pixelWriter.setColor(xPos + 1, yPos - 1, markIndividual);
					pixelWriter.setColor(xPos + 1, yPos, markIndividual);
					pixelWriter.setColor(xPos + 1, yPos + 1, markIndividual);

				}
			}
			individualSheepLabel.setText(Integer.toString(individualSheep));
			sheepGroupsLabel.setText(Integer.toString(sheepGroups));
			greyscaleImage.setImage(writableGSImage);
		}

		sheepFromGroups = sheepFromGroups / sheepSize;
		System.out.println(sheepFromGroups);
		int totalSheep = 0;
		totalSheep = individualSheep + sheepFromGroups;
		totalSheepLabel.setText(Integer.toString(totalSheep));

	}

	@Override
	public void start(Stage primaryStage) {
		try {
			window = primaryStage;
			loader = new FXMLLoader(getClass().getResource("imageView.fxml"));

			Pane root = (Pane) loader.load();
			Scene scene = new Scene(root, 1000, 600);

			window.setMinWidth(1000);
			window.setMinHeight(600);
			window.setMaxWidth(1000);
			window.setMaxHeight(600);
			window.setScene(scene);
			window.setTitle("Worksheet 1 (Written by Andrew Bates)");
			window.show();

		} catch (IOException e) {
		}
	}

}
