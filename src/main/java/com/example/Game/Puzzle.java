package com.example.Game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import com.example.Main;
import com.example.Piece.Bishop;
import com.example.Piece.King;
import com.example.Piece.Knight;
import com.example.Piece.Pawn;
import com.example.Piece.Piece;
import com.example.Piece.Queen;
import com.example.Piece.Rook;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Puzzle extends Rule {
	static List<String> fenAndMoves = getFenAndMoves();
	static int currentColor = getCurrentColor();
	static int botColor = currentColor;
	static int startRow;
	static int startCol;
	static int endRow;
	static int endCol;
	static String[] moves;
	static int turn = 0;
	static int moveIndex = 0;
	private static boolean win;
	public Puzzle(GraphicsContext gc1 , Mouse mouse, Canvas canvas){
	    gc = gc1;
	    c = canvas;
	    setPieces(fenAndMoves.get(0));
	    game_mouse = mouse;
	    copyPieces(pieces, simPieces);
	}
	private static List<String> getFenAndMoves() {
		List<String> lines;
	    try {
	        lines = Files.readAllLines(Paths.get("C:\\Users\\User\\OneDrive - Hanoi University of Science and Technology\\Documents\\L\\GameChess\\src\\data\\lichess_db_puzzle.csv"), StandardCharsets.UTF_8);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	    
	    Random rand = new Random();
	    int randomIndex = rand.nextInt(lines.size());
	    // Split the line into fields and get the FEN string
	    String line = lines.get(randomIndex);
	    String[] fields = line.split(",");
	    List<String> fenAndMoves = new ArrayList<>();
	    fenAndMoves.add(fields[1]);
	    fenAndMoves.add(fields[2]);
	    System.out.println(fenAndMoves);
	    return fenAndMoves;
	}
	
	
	public static int getCurrentColor() {
		String fen = fenAndMoves.get(0);
		String[] row = fen.split("/");
		String lastRow = row[7];
		int currentColor = WHITE;
		for(int i = 0; i < lastRow.length(); i++) {
			if(lastRow.charAt(i) == ' ') {
				currentColor = (lastRow.charAt(i+1) == 'w') ? WHITE : BLACK;
				break;
			}
		}
		return currentColor;
	}
	public int getColFromMove(char col) {
    	int column = 0;
    	switch(col) {
    		case 'a':
    			column = 0;
    			break;
    		case 'b':
    			column = 1;
    			break;
    		case 'c':
    			column = 2;
    			break;
    		case 'd':
    			column = 3;
    			break;
    		case 'e':
    			column = 4;
    			break;
    		case 'f':
    			column = 5;
    			break;
    		case 'g':
    			column = 6;
    			break;
    		case 'h':
    			column = 7;
    			break;
    	}	
    	return column;
    }
	
	private void changeTurnPuzzle(){
        currentColor = (currentColor == WHITE) ? BLACK : WHITE;
        for(Piece p: simPieces){
            if( currentColor == WHITE && p.color == WHITE){
                p._2squareMove = false;
            }
            if( currentColor == BLACK && p.color == BLACK){
                p._2squareMove = false;
            }
        }
    }
	
	private void botCapture(int endRow, int endCol) {
		int i = 0;
		while(true) {
			if(i == simPieces.size() - 1) break;
			Piece testPiece = simPieces.get(i);
			if(testPiece.col == endCol && testPiece.row == endRow) {
				simPieces.remove(i);
				break;
			}
			i++;
		}
	}
	private int botMove(Piece testPiece, int startRow, int startCol, int endRow, int endCol) {
		if(testPiece instanceof Pawn && testPiece.color == botColor && testPiece.col == startCol && testPiece.row == startRow) {
			simPieces.remove(testPiece.getIndex());
			simPieces.add(new Pawn(botColor, endRow, endCol));
			return 1;
		}
		if(testPiece instanceof Rook && testPiece.color == botColor && testPiece.col == startCol && testPiece.row == startRow) {
			simPieces.remove(testPiece.getIndex());
			simPieces.add(new Rook(botColor, endRow, endCol));
			return 1;
		}if(testPiece instanceof Knight && testPiece.color == botColor && testPiece.col == startCol && testPiece.row == startRow) {
			simPieces.remove(testPiece.getIndex());
			simPieces.add(new Knight(botColor, endRow, endCol));
			return 1;
		}if(testPiece instanceof Bishop && testPiece.color == botColor && testPiece.col == startCol && testPiece.row == startRow) {
			simPieces.remove(testPiece.getIndex());
			simPieces.add(new Bishop(botColor, endRow, endCol));
			return 1;
		}if(testPiece instanceof Queen && testPiece.color == botColor && testPiece.col == startCol && testPiece.row == startRow) {
			simPieces.remove(testPiece.getIndex());
			simPieces.add(new Queen(botColor, endRow, endCol));
			return 1;
		}if(testPiece instanceof King && testPiece.color == botColor && testPiece.col == startCol && testPiece.row == startRow) {
			simPieces.remove(testPiece.getIndex());
			simPieces.add(new King(botColor, endRow, endCol));
			return 1;
		}
		return 0;
	}
		
    public void gameloop(){
    	moveIndex = 0;
    	moves = fenAndMoves.get(1).split(" ");
    			new AnimationTimer() {
    				double drawInterval = 1000000000 / FPS;
    				double delta = 0;
    				long LastTime = System.nanoTime();
    				@Override
    				public void handle(long now) {
    					delta += (now - LastTime) / drawInterval;
    					LastTime = now;
    					if(moveIndex == moves.length ) {
    						stop();
    					}
    					else {
    					String move = moves[moveIndex];
    					startCol = getColFromMove(move.charAt(0));
    					startRow = 8 - Character.getNumericValue(move.charAt(1));
    					endCol = getColFromMove(move.charAt(2));
    					endRow = 8 - Character.getNumericValue(move.charAt(3));
    						if(delta >= 1){
	    						if(currentColor != botColor) {
	    							update();
	    						}
	    						else {
	    							int i = 0;
	    							while(true) {
	    								if(i == (simPieces.size() - 1)) break;
	    								Piece testPiece = simPieces.get(i);
	    								botCapture(endRow, endCol);
	    								int isMoved = botMove(testPiece, startRow, startCol, endRow, endCol);
	    								if(isMoved == 1) break;
	    								i++;
	    							}
	    							copyPieces(simPieces, pieces);
	    							changeTurnPuzzle();
	    							moveIndex++;
	    						}
	    						gc.clearRect(0, 0, c.getWidth(), c.getHeight());
	    						render();
	    						delta--;
	    					}
    					}
//    				}
    				}
    			}.start();
    		}

    static public void reset(){
        currentColor = getCurrentColor();
        pieces.clear();
        simPieces.clear();
        promoPieces.clear();
        activeP = null;
        checkingP = null;
        castlingP = null;
        all_move.clear();
        isPromo = false;
        gameOver = false;
        stalemate = false;
        setPieces(fenAndMoves.get(0));
        copyPieces(pieces, simPieces);
    }
    
    public void gameOver() {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Wrong move.");
        alert.setContentText("Do you want to play again?");
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Platform.runLater(() -> {
	        alert.showAndWait().ifPresent(response -> {
	            if (response == buttonTypeYes) {
	                reset();
	                gameloop();
	            } else if (response == buttonTypeNo) {
	                System.exit(1);
	            }
	        });
        });
    }
    
	private void update(){
    			if(isPromo){
    				promoting();
    			}else if(!gameOver && !stalemate){
    				if(game_mouse.isPressed()){
    					
    					if(activeP == null){
    						for(Piece p: simPieces){
    							if( p.color == currentColor && p.row == game_mouse.y / Board.SQUARE_SIZE && p.col == game_mouse.x / Board.SQUARE_SIZE){
    								activeP = p;
    							}
    						}
    						if(activeP != null) getAllMove();
    					}
    					else{
    						// if player holding a piece
    						simulate();
    					}
    				}
    				if(!game_mouse.isPressed()){
    					//if player release the piece
    					if(activeP != null){
    						if(activeP.isSamePosition(activeP.row, activeP.col)){
    							activeP.resetPosition();                
    						}
    						else{
    							// Move is confirmed
    							if(validSquare){
    								copyPieces(simPieces, pieces);
    								if(activeP.preRow != startRow || activeP.preCol != startCol || 
    									activeP.row != endRow || activeP.col != endCol) {
    									gameOver();
    								}
    								else {
	    									if(moveIndex == moves.length - 1) {
	    										win = true;
	    									}
    								}
    								activeP.updatePosition();
    								// Check end game condition
    								if(isKingInCheck() && isCheckmate()){
    									gameOver = true;
    								}
    								else if(isKingInCheck() &&isStalemate()){
    									stalemate = true;
    								}
    								else{
    									// normal move
    									if(castlingP != null){
    										castlingP.updatePosition();
    									}
    									if(checkPromo()){
    										isPromo = true;
    									}else{
    										activeP = null;
    										changeTurnPuzzle();
    										moveIndex++;
    									}
    								}
    							}
    							else{
    								// Move is not completed
    								copyPieces(pieces, simPieces);
    								activeP.resetPosition();
    								activeP = null;
    							}
    						}
    					}
    				}
    			}
    			else if(gameOver || stalemate){
    				// this mouse event can be modified to any event
    				if(game_mouse.isPressed()){
    					reset();
    				}
    			}
    		}
    	
    
    private void simulate(){
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        // reset the position of the castling rook
        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        activeP.x = game_mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = game_mouse.y - Board.HALF_SQUARE_SIZE;
        
        if(game_mouse.x <= Board.HALF_SQUARE_SIZE ){
            activeP.x = 0;
            if(game_mouse.y <= Board.HALF_SQUARE_SIZE) activeP.y = 0;
            else if(game_mouse.y >= Board.SQUARE_SIZE * Board.MAX_ROW - Board.HALF_SQUARE_SIZE) activeP.y = Board.SQUARE_SIZE * Board.MAX_ROW - Board.SQUARE_SIZE;
        }else if(game_mouse.x >= Board.SQUARE_SIZE * Board.MAX_COL - Board.HALF_SQUARE_SIZE){
            activeP.x = Board.SQUARE_SIZE * Board.MAX_COL - Board.SQUARE_SIZE;
            if(game_mouse.y <= Board.HALF_SQUARE_SIZE) activeP.y = 0;
            else if(game_mouse.y >= Board.SQUARE_SIZE * Board.MAX_ROW - Board.HALF_SQUARE_SIZE) activeP.y = Board.SQUARE_SIZE * Board.MAX_ROW - Board.SQUARE_SIZE;
        }else if (game_mouse.y <= Board.HALF_SQUARE_SIZE ){
            activeP.y = 0;
            if(game_mouse.x <= Board.HALF_SQUARE_SIZE) activeP.x = 0;
            else if(game_mouse.x >= Board.SQUARE_SIZE * Board.MAX_COL - Board.HALF_SQUARE_SIZE) activeP.x = Board.SQUARE_SIZE * Board.MAX_COL - Board.SQUARE_SIZE;
        }else if(game_mouse.y >= Board.SQUARE_SIZE * Board.MAX_ROW - Board.HALF_SQUARE_SIZE ){
            activeP.y = Board.SQUARE_SIZE * Board.MAX_ROW - Board.SQUARE_SIZE;
            if(game_mouse.x <= Board.HALF_SQUARE_SIZE) activeP.x = 0;
            else if(game_mouse.x >= Board.SQUARE_SIZE * Board.MAX_COL - Board.HALF_SQUARE_SIZE) activeP.x = Board.SQUARE_SIZE * Board.MAX_COL - Board.SQUARE_SIZE;
        }

        activeP.row = activeP.getRow(activeP.y);
        activeP.col = activeP.getCol(activeP.x);

        // Check if the piece can move to the target square
        if(activeP.canMove(activeP.row, activeP.col)){
            canMove = true;
            
            // if player is holding a piece and moving through other pieces, they will be removed from the simPieces
            // That the reason why we need to reset the simPieces after any loop
            if(activeP.affectedP != null){
                simPieces.remove(activeP.affectedP.getIndex());
            }
            castling();
            // if player's king is not in check after the opponent's move, the move is valid
            if(!isKingIllegalMove(activeP) && opponentCanCaptureKing() == false){
                validSquare = true;
            }
        }
    }
    
    private void render(){
        //Draw Background
        gc.drawImage(background, 0, 0, Main.WIDTH, Main.HEIGHT);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(10);
        gc.strokeLine(800, 0, 800, Main.HEIGHT);

        //Draw Board
        Board.draw(gc);

        //Draw pieces
        for(Piece p : pieces){
            p.draw(gc);
        }
        
        //Active Piece
        if(activeP != null){
            gc.setGlobalAlpha(0.3); // Set opacity to 50%
            gc.setFill(Color.WHITE);
            gc.fillRect(activeP.preCol * Board.SQUARE_SIZE, activeP.preRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
            gc.setGlobalAlpha(0.7);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(Board.SQUARE_SIZE / 20);
            int arcSize = 20;
            gc.strokeRoundRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE, arcSize , arcSize);
            
            if(!all_move.isEmpty()){
                for(Pair<Integer, Integer> pos: all_move){
                    int row = (int) pos.getKey();
                    int col = (int) pos.getValue();
                    boolean hit = false;
                    gc.setFill(Color.BLACK);
                    gc.setGlobalAlpha(0.3);
                    for(Piece p: pieces){
                        if(p.row == row && p.col == col && p.color != activeP.color){
                            hit = true;
                        }
                    }
                    if(hit){
                        int temp = Board.HALF_SQUARE_SIZE / 15;
                        gc.setStroke(Color.BLACK);
                        gc.setLineWidth(Board.SQUARE_SIZE / 12);
                        gc.strokeOval(col * Board.SQUARE_SIZE + temp, row * Board.SQUARE_SIZE+temp, Board.SQUARE_SIZE- 2*temp, Board.SQUARE_SIZE-2*temp);
                    }else gc.fillOval(col * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE/5 *3, row * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE / 5 *3, Board.SQUARE_SIZE/10 *4, Board.SQUARE_SIZE/10 *4);;
                }
            }
            gc.setGlobalAlpha(1);
            activeP.draw(gc);
        }

        //MESSAGE
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gc.setFontSmoothingType(FontSmoothingType .LCD);
        if(win) {
            gc.setLineWidth(200);
            gc.setStroke(Color.WHITE);
            gc.setGlobalAlpha(0.5);
            gc.strokeLine(0, Main.HEIGHT/2, Main.WIDTH, Main.HEIGHT/2);
            gc.setGlobalAlpha(1);
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 100));
            if(currentColor == WHITE){
                gc.fillText("BLACK WINS", 200, Main.HEIGHT/2 +35);
            }else{
                gc.fillText("WHITE WINS", 200, Main.HEIGHT/2 +35);
            }
    
        }
        if(stalemate){
            gc.setLineWidth(200);
            gc.setStroke(Color.WHITE);
            gc.setGlobalAlpha(0.5);
            gc.strokeLine(0, Main.HEIGHT/2, Main.WIDTH, Main.HEIGHT/2);
            gc.setGlobalAlpha(1);
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 100));
            gc.fillText("STALEMATE", 200, Main.HEIGHT/2 +35);
        }
        else{
            if(isPromo){
                gc.fillText("Promote to: ", 850, 180);
                for(Piece p: promoPieces){
                    p.draw(gc);
                }
            }
            // NORMAL
            else{
                if(currentColor == WHITE){
                    gc.fillText("WHITE TURN", 850, 650);
                    if( checkingP != null && checkingP.color == BLACK){
                        gc.setFill(Color.RED);
                        gc.fillText("The King", 880, 380);
                        gc.fillText("is in Check", 870, 420);
                    }
                } else {
                    gc.fillText("BLACK TURN", 850, 150);
                    if( checkingP != null && checkingP.color == WHITE){
                        gc.setFill(Color.RED);
                        gc.fillText("The King", 880, 380);
                        gc.fillText("is in Check", 870, 420);
                    }
                }
            }
        }
    }
}