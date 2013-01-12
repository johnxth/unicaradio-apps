//
//  FirstViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../models/TrackInfos.h"

#pragma mark - Constants

#define PLAY_IMAGE_NORMAL @"play_normal.png"
#define PAUSE_IMAGE_0 @"pause_0.png"
#define WAIT_IMAGE_NORMAL @"wait.png"
#define PLAY_IMAGE_PRESSED @"play_pressed.png"
#define PAUSE_IMAGE_PRESSED @"pause_pressed.png"
#define WAIT_IMAGE_PRESSED @"wait_pressed.png"
#define DEFAULT_COVER_FILENAME @"cover.png"

#define SERVER_URL @"http://streaming.unicaradio.it:80/unica64.aac"
#define COVER_URL @"http://www.unicaradio.it/regia/OnAir.jpg"

#pragma mark - Interface

@class AudioStreamer;
@class MarqueeLabel;
@class SettingsManager;

@interface StreamingViewController : UIViewController
{
    AudioStreamer *streamer;

    IBOutlet UILabel *titleLabel;
    IBOutlet UILabel *singerLabel;
    IBOutlet UIButton *playPauseButton;
    IBOutlet UIImageView *coverImageView;

	MarqueeLabel *titleMarqueeLabel;
	MarqueeLabel *singerMarqueeLabel;

	TrackInfos *infos;
	TrackInfos *oldInfos;

	UIPopoverController *popover;

	UIInterfaceOrientation oldOrientation;

	SettingsManager *settingsManager;
}

#pragma mark - Actions
- (IBAction) playOrPause:(id)sender;

#pragma mark - Functions
- (void) updateUi;

#pragma mark - Properties
@property (nonatomic, strong) IBOutlet UILabel *titleLabel;
@property (nonatomic, strong) IBOutlet UILabel *singerLabel;
@property (nonatomic, strong) IBOutlet UIButton *playPauseButton;
@property (nonatomic, strong) IBOutlet UIImageView *coverImageView;

@property (strong) TrackInfos *infos;
@property (strong) TrackInfos *oldInfos;

@property (strong) UIPopoverController *popover;

@property (strong, nonatomic) SettingsManager *settingsManager;

@end
