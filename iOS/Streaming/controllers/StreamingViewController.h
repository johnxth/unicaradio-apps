//
//  FirstViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AudioStreamer.h"

#define PLAY_IMAGE_NORMAL @"play_normal.png"
#define PAUSE_IMAGE_NORMAL @"pause_0.png"
#define PLAY_IMAGE_PRESSED @"play_pressed.png"
#define PAUSE_IMAGE_PRESSED @"pause_pressed.png"
#define DEFAULT_COVER_FILENAME @"cover.png"

#define SERVER_URL @"http://streaming.unicaradio.it:80/unica64.aac"
#define COVER_URL @"http://www.unicaradio.it/regia/OnAir.jpg"

@interface StreamingViewController : UIViewController
{
    AudioStreamer *streamer;
    //NSURL *SERVER_URL;
    
    IBOutlet UILabel *titleLabel;
    IBOutlet UILabel *singerLabel;
    IBOutlet UIButton *playPauseButton;
    IBOutlet UIImageView *coverImageView;
    
    NSString *currentArtist;
	NSString *currentTitle;
}

#pragma mark - Actions
- (IBAction) playOrPause:(id)sender;

#pragma mark - Functions
- (void) updateUi;

#pragma mark - Properties
@property (nonatomic, retain) IBOutlet UILabel *titleLabel;
@property (nonatomic, retain) IBOutlet UILabel *singerLabel;
@property (nonatomic, retain) IBOutlet UIButton *playPauseButton;
@property (nonatomic, retain) IBOutlet UIImageView *coverImageView;

@property (retain) NSString *currentArtist;
@property (retain) NSString *currentTitle;

@end
